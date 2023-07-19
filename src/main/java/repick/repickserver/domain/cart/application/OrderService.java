package repick.repickserver.domain.cart.application;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import repick.repickserver.domain.cart.dao.OrderRepository;
import repick.repickserver.domain.cart.dao.OrderStateRepository;
import repick.repickserver.domain.cart.domain.Order;
import repick.repickserver.domain.cart.domain.OrderState;
import repick.repickserver.domain.cart.dto.OrderRequest;
import repick.repickserver.domain.cart.dto.OrderResponse;
import repick.repickserver.domain.member.dao.MemberRepository;
import repick.repickserver.domain.ordernumber.application.OrderNumberService;
import repick.repickserver.domain.product.dao.ProductRepository;
import repick.repickserver.domain.product.domain.Product;
import repick.repickserver.global.error.exception.CustomException;
import static repick.repickserver.domain.cart.domain.OrderCurrentState.UNPAID;
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

    public OrderResponse createOrder(OrderRequest orderRequest) {
        String orderNumber = orderNumberService.generateOrderNumber(ORDER);

        for (Long productId : orderRequest.getProductIds()) {
            // 요청한 상품의 품절/삭제 여부 확인 후 상태 변경 (입금 대기중일 때 다른 사람에게 판매되면 안되기 때문)
            Product product = productRepository.findByIdAndProductState(productId, SELLING)
                    .orElseThrow(() -> new CustomException(PRODUCT_NOT_SELLING));

            product.changeProductState(SOLD_OUT);

            // 바로 구매하는 상품이 아니라 마이픽 상품이라면, CartProductState 가 IN_CART 또는 HOME_FITTING_REQUESTED 일 것임 -> ORDERED 로 변경


            // 구독자여서 홈피팅한 상품이라면, HomeFittingState 가 REQUESTED, DELIVERING, DELIVERED, RETURN_REQUESTED, RETURNED 중 하나일 것임 -> PURCHASED 로 변경
        }

        // 주문 생성
        Order order = Order.builder()
                .member(memberRepository.findById(orderRequest.getMemberId()).orElseThrow(() -> new CustomException(MEMBER_NOT_FOUND)))
                .personName(orderRequest.getPersonName())
                .phoneNumber(orderRequest.getPhoneNumber())
                .address(orderRequest.getAddress())
                .requestDetail(orderRequest.getRequestDetail())
                .orderNumber(orderNumber)
                .build();

        OrderState orderState = OrderState.builder()
                .orderCurrentState(UNPAID)
                .build();

        order.addOrderState(orderState); // 양쪽 다 매핑

        orderRepository.save(order);
        orderStateRepository.save(orderState);

        return OrderResponse.builder()
                .order(order)
                .build();
    }
}

/**
 * 주문 상태 변경 (관리자)
 * 입금 확인 시 배송 준비중으로 변경
 */