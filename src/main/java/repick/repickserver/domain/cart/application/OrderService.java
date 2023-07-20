package repick.repickserver.domain.cart.application;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import repick.repickserver.domain.cart.dao.*;
import repick.repickserver.domain.cart.domain.*;
import repick.repickserver.domain.cart.dto.OrderRequest;
import repick.repickserver.domain.cart.dto.OrderResponse;
import repick.repickserver.domain.cart.dto.OrderStateResponse;
import repick.repickserver.domain.cart.dto.UpdateOrderStateRequest;
import repick.repickserver.domain.member.dao.MemberRepository;
import repick.repickserver.domain.ordernumber.application.OrderNumberService;
import repick.repickserver.domain.product.dao.ProductRepository;
import repick.repickserver.domain.product.domain.Product;
import repick.repickserver.global.error.exception.CustomException;
import repick.repickserver.global.jwt.JwtProvider;
import repick.repickserver.infra.SlackNotifier;

import java.util.List;
import java.util.stream.Collectors;
import static repick.repickserver.domain.cart.domain.CartProductState.ORDERED;
import static repick.repickserver.domain.cart.domain.OrderCurrentState.UNPAID;
import static repick.repickserver.domain.cart.domain.HomeFittingState.*;
import static repick.repickserver.domain.ordernumber.domain.OrderType.ORDER;
import static repick.repickserver.domain.product.domain.ProductState.*;
import static repick.repickserver.global.error.exception.ErrorCode.*;

@Service
@Transactional
@RequiredArgsConstructor
public class OrderService {
    private final OrderNumberService orderNumberService;
    private final OrderRepository orderRepository;
    private final OrderStateRepository orderStateRepository;
    private final MemberRepository memberRepository;
    private final ProductRepository productRepository;
    private final CartProductRepository cartProductRepository;
    private final CartRepository cartRepository;
    private final HomeFittingRepository homeFittingRepository;
    private final JwtProvider jwtProvider;
    private final OrderProductRepository orderProductRepository;
    private final SlackNotifier slackNotifier;

    public OrderResponse createOrder(OrderRequest orderRequest, String token) {
        String orderNumber = orderNumberService.generateOrderNumber(ORDER);

        for (Long productId : orderRequest.getProductIds()) {
                // 요청한 상품의 품절/삭제 여부 확인 후 상태 변경 (입금 대기중일 때 다른 사람에게 판매되면 안되기 때문)
                Product product = productRepository.findByIdAndProductState(productId, SELLING)
                        .orElseThrow(() -> new CustomException(PRODUCT_NOT_SELLING));

                product.changeProductState(SOLD_OUT);

                // 바로 구매하는 상품이 아니라 마이픽 상품이라면, CartProductState 가 IN_CART 또는 HOME_FITTING_REQUESTED 일 것임 -> ORDERED 로 변경
                Cart cart = cartRepository.findByMember(jwtProvider.getMemberByRawToken(token));
                cartProductRepository.findByCartIdAndProductId(cart.getId(), product.getId())
                        .ifPresent(cartProduct -> cartProduct.changeState(ORDERED));

                // 구독자여서 홈피팅한 상품이라면, HomeFittingState 가 REQUESTED, DELIVERING, DELIVERED, RETURN_REQUESTED, RETURNED 중 하나일 것임 -> PURCHASED 로 변경
                homeFittingRepository.findHomeFittingByCartIdAndProductId(cart.getId(), product.getId())
                        .ifPresent(homeFitting -> homeFitting.changeState(PURCHASED));

        }

        // 주문, 주문 상태 저장
        Order order = Order.builder()
                .member(memberRepository.findById(orderRequest.getMemberId()).orElseThrow(() -> new CustomException(MEMBER_NOT_FOUND)))
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

        /*
        * 주문 알림
        * 주문자 이름, 주소, 전화번호
        * 주문 상품 이름과 가격
        * 모든 상품들에 대해 forEach
         */
        StringBuilder sb = new StringBuilder();
        sb.append("신청자: ").append(orderRequest.getPersonName()).append("\n");
        sb.append("연락처: ").append(orderRequest.getPhoneNumber()).append("\n");
        sb.append("주소: ").append(orderRequest.getAddress().mainAddress).append("\n");
        sb.append("상세 주소: ").append(orderRequest.getAddress().detailAddress).append("\n");
        sb.append("우편 번호: ").append(orderRequest.getAddress().zipCode).append("\n");
        sb.append("주문 상품: ").append("\n");
        orderProducts.forEach(orderProduct -> sb.append(orderProduct.getProduct().getName()).append(" ").append(orderProduct.getProduct().getPrice()).append("\n"));
        slackNotifier.sendSlackNotification(sb.toString());

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

        return OrderStateResponse.builder()
                .orderNumber(order.getOrderNumber())
                .orderId(order.getId())
                .orderStateId(savedOrderState.getId())
                .orderCurrentState(savedOrderState.getOrderCurrentState())
                .build();
    }
}
