package repick.repickserver.domain.cart.dto;

import com.querydsl.core.annotations.QueryProjection;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import repick.repickserver.domain.cart.domain.Order;
import repick.repickserver.domain.cart.domain.OrderCurrentState;
import repick.repickserver.domain.cart.domain.OrderState;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OrderStateResponse {

    private String orderNumber;
    private Long orderId;
    private Long orderStateId;
    private OrderCurrentState orderCurrentState;

    @Builder
    public OrderStateResponse(String orderNumber, Long orderId, Long orderStateId, OrderCurrentState orderCurrentState) {
        this.orderNumber = orderNumber;
        this.orderId = orderId;
        this.orderStateId = orderStateId;
        this.orderCurrentState = orderCurrentState;
    }

    @QueryProjection
    @Builder
    public OrderStateResponse(Order order, OrderState orderState) {
        this.orderNumber = order.getOrderNumber();
        this.orderId = order.getId();
        this.orderStateId = orderState.getId();
        this.orderCurrentState = orderState.getOrderCurrentState();
    }
}
