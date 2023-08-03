package repick.repickserver.domain.cart.application;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import repick.repickserver.domain.cart.dao.*;
import repick.repickserver.domain.cart.domain.*;
import repick.repickserver.domain.cart.dto.OrderRequest;
import repick.repickserver.domain.cart.dto.OrderResponse;
import repick.repickserver.domain.cart.dto.OrderStateResponse;
import repick.repickserver.domain.cart.dto.UpdateOrderStateRequest;
import repick.repickserver.domain.member.domain.Member;
import repick.repickserver.domain.ordernumber.application.OrderNumberService;
import repick.repickserver.domain.product.dao.ProductRepository;
import repick.repickserver.domain.product.domain.Product;
import repick.repickserver.global.config.SmsProperties;
import repick.repickserver.global.error.exception.CustomException;
import repick.repickserver.global.jwt.JwtProvider;
import repick.repickserver.infra.slack.application.SlackNotifier;
import repick.repickserver.infra.slack.mapper.SlackMapper;
import repick.repickserver.infra.sms.SmsSender;
import repick.repickserver.infra.sms.model.Message;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

import static repick.repickserver.domain.cart.domain.CartProductState.HOME_FITTING_REQUESTED;
import static repick.repickserver.domain.cart.domain.CartProductState.ORDERED;
import static repick.repickserver.domain.cart.domain.HomeFittingState.PURCHASED;
import static repick.repickserver.domain.cart.domain.OrderCurrentState.UNPAID;
import static repick.repickserver.domain.ordernumber.domain.OrderType.ORDER;
import static repick.repickserver.domain.product.domain.ProductState.*;
import static repick.repickserver.global.error.exception.ErrorCode.*;
import static repick.repickserver.global.util.formatPhoneNumber.removeHyphens;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class OrderService {
    private final OrderNumberService orderNumberService;
    private final OrderRepository orderRepository;
    private final OrderStateRepository orderStateRepository;
    private final ProductRepository productRepository;
    private final CartProductRepository cartProductRepository;
    private final CartRepository cartRepository;
    private final HomeFittingRepository homeFittingRepository;
    private final JwtProvider jwtProvider;
    private final OrderProductRepository orderProductRepository;
    private final SlackNotifier slackNotifier;
    private final SmsSender smsSender;
    private final SmsProperties smsProperties;
    private final SlackMapper slackMapper;

    public OrderResponse createOrder(OrderRequest orderRequest, String token) {
        String orderNumber = orderNumberService.generateOrderNumber(ORDER);

        // 신청자의 장바구니 가져옴
        Cart cart = cartRepository.findByMember(jwtProvider.getMemberByRawToken(token));
        cartProductRepository.findByCartIdAndProductIdAndCartProductState(cart.getId(), orderRequest.getProductIds().get(0), HOME_FITTING_REQUESTED)
                .ifPresent(cartProduct -> {
                    // 존재할 경우 해당 주문은 홈피팅 주문이다 : 해당 홈피팅 주문번호를 찾고, 홈피팅 주문번호가 같은 상품들 모두 RETURN_REQUESTED 로 변경
                    HomeFitting homeFitting = homeFittingRepository.findByCartProductId(cartProduct.getId());
                    homeFittingRepository.findByOrderNumber(homeFitting.getOrderNumber())
                            .forEach(hf -> hf.changeState(HomeFittingState.RETURN_REQUESTED));
                });

        for (Long productId : orderRequest.getProductIds()) {
                // 요청한 상품의 품절/삭제 여부 확인 후 상태 변경 (입금 대기중일 때 다른 사람에게 판매되면 안되기 때문)
                Product product = productRepository.findByIdAndProductState(productId, PENDING)
                        .orElseThrow(() -> new CustomException(PRODUCT_NOT_SELLING));

                // 주문 상태를 ORDERED 로 변경 : 다른 사람이 구매 신청할 수 없게 하기 위함
                product.changeProductState(PENDING);

                // 바로 구매하는 상품이 아니라 마이픽 상품이라면, CartProductState 가 IN_CART 또는 HOME_FITTING_REQUESTED 일 것임 -> ORDERED 로 변경
                cartProductRepository.findByCartIdAndProductIdAndIsNotDeleted(cart.getId(), product.getId())
                        .ifPresent(cartProduct -> cartProduct.changeState(ORDERED));

                // 구독자여서 홈피팅한 상품이라면, HomeFittingState 가 REQUESTED, DELIVERING, DELIVERED, RETURN_REQUESTED, RETURNED 중 하나일 것임 -> PURCHASED 로 변경
                homeFittingRepository.findHomeFittingByCartIdAndProductId(cart.getId(), product.getId())
                        .ifPresent(homeFitting -> homeFitting.changeState(PURCHASED));

        }

        // 토큰으로 멤버 찾기
        Member member = jwtProvider.getMemberByRawToken(token);

        // 주문, 주문 상태 저장
        Order order = Order.builder()
                .member(member)
                .personName(orderRequest.getPersonName())
                .phoneNumber(orderRequest.getPhoneNumber())
                .address(orderRequest.getAddress())
                .requestDetail(orderRequest.getRequestDetail())
                .orderNumber(orderNumber)
                .build();

        Order savedOrder = orderRepository.save(order);

        OrderState orderState = OrderState.builder()
                .order(order)
                .orderCurrentState(UNPAID)
                .build();

        OrderState savedOrderState = orderStateRepository.save(orderState);

        // 주문 상품 저장
        List<Long> productIds = orderRequest.getProductIds();
        List<OrderProduct> orderProducts = productIds.stream()
                .map(productId -> productRepository.findById(productId)
                        .orElseThrow(() -> new CustomException(INTERNAL_SERVER_ERROR)))
                .map(product -> OrderProduct.builder()
                        .order(order)
                        .product(product)
                        .build())
                .map(orderProductRepository::save)
                .collect(Collectors.toList());

        // 슬랙 알림
        try {
            slackNotifier.sendOrderSlackNotification(slackMapper.toOrderSlackNoticeString(orderNumber, orderRequest, orderProducts));
        }
        catch (Exception e) {
            log.error("Slack 알림 전송 실패");
        }


        /**
         * 주문 알림 문자 발송
         * 주문 내역, 무통장입금 계좌번호, 입금 기한 안내
         */
        try {
            smsSender.sendSms(Message.builder()
                    .to(removeHyphens(orderRequest.getPhoneNumber()))
                    .content("[리픽]" + "\n\n" + "안녕하세요, " + orderRequest.getPersonName() + "님 :)\n\n" +
                            "주문일: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy년 MM월 dd일")) + "\n" +
                            "주문 금액: " + orderProducts.stream()
                                    .mapToLong(orderProduct -> orderProduct.getProduct().getPrice())
                                    .sum() + "원\n" +
                            "주문 번호: " + orderNumber + "\n\n" +
                            "주문 내역이 접수되었습니다.\n\n" +
                            "입금 계좌: " + smsProperties.getBankName() + " " + smsProperties.getBankAccount() + "\n" +
                            "입금 기한: " + LocalDateTime.now().plusDays(1).format(DateTimeFormatter.ofPattern("yyyy년 MM월 dd일")) + "\n\n" +
                            "입금 확인 후, 빠른 출고 도와드릴 수 있도록 노력하겠습니다.\n" +
                            "감사합니다 ♥")
                    .build());
        }
        catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new CustomException(SMS_SEND_FAILED);
        }

        return OrderResponse.builder()
                .order(savedOrder)
                .orderState(savedOrderState)
                .orderProducts(orderProducts)
                .build();
    }

    public OrderStateResponse updateOrderState(UpdateOrderStateRequest request) {
        // 주문번호로 주문 조회
        Order order = orderRepository.findByOrderNumber(request.getOrderNumber())
                .orElseThrow(() -> new CustomException(ORDER_NOT_FOUND));

        OrderState savedOrderState = orderStateRepository.save(OrderState.builder()
                .order(order)
                .orderCurrentState(OrderCurrentState.valueOf(request.getOrderState()))
                .build());

        // Order에서 Product를 모두 조회
        orderProductRepository.findByOrderId(order.getId()).forEach(orderProduct -> {
            // 주문 신청이 없는 상품의 경우 예외 발생
            if (orderProduct.getProduct().getProductState() == SELLING)
                throw new CustomException(PRODUCT_NOT_PENDING);

            // 만약 Product의 State가 SELLING이라면 SOLD_OUT으로 변경
            // (정산 신청한 상태가 변경되는 것을 방지
            if (orderProduct.getProduct().getProductState() == PENDING) {
                orderProduct.getProduct().changeProductState(SOLD_OUT);
            }
        });

        return OrderStateResponse.builder()
                .order(order)
                .orderStateId(savedOrderState.getId())
                .orderCurrentState(savedOrderState.getOrderCurrentState())
                .build();
    }

    public List<OrderStateResponse> getOrderStates(String orderState) {
        List<OrderStateResponse> orderStates = orderStateRepository.getOrderStates(orderState);
        if(orderStates.isEmpty()) {
            throw new CustomException(ORDER_STATE_NOT_FOUND);
        }
        return orderStates;
    }
}
