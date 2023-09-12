package repick.repickserver.domain.order.dto;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import repick.repickserver.domain.order.domain.Order;
import repick.repickserver.domain.order.domain.OrderCurrentState;
import repick.repickserver.domain.order.domain.OrderProduct;
import repick.repickserver.domain.order.domain.OrderState;
import repick.repickserver.domain.model.Address;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OrderResponse {

    private Long id;

    private Long memberId;

    private String personName;

    private String phoneNumber;

    private Address address;

    private String requestDetail;

    private OrderCurrentState orderCurrentState;

    private String orderNumber;

    private List<Long> productIds;

    @Builder
    public OrderResponse(Order order, OrderState orderState, List<OrderProduct> orderProducts) {
        this.id = order.getId();
        this.memberId = order.getMember().getId();
        this.personName = order.getPersonName();
        this.phoneNumber = order.getPhoneNumber();
        this.address = order.getAddress();
        this.requestDetail = order.getRequestDetail();
        this.orderCurrentState = orderState.getOrderCurrentState();
        this.orderNumber = order.getOrderNumber();
        this.productIds = orderProducts.stream()
                .map(orderProduct -> orderProduct.getProduct().getId())
                .collect(Collectors.toList());
    }

    public static OrderResponse from(Order order, OrderState orderState, List<OrderProduct> orderProducts) {
        return OrderResponse.builder()
                .order(order)
                .orderState(orderState)
                .orderProducts(orderProducts)
                .build();
    }
}
