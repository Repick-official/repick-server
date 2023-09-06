package repick.repickserver.domain.order.dto;

import com.querydsl.core.annotations.QueryProjection;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import repick.repickserver.domain.order.domain.Order;
import repick.repickserver.domain.order.domain.OrderCurrentState;
import repick.repickserver.domain.order.domain.OrderState;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OrderStateResponse {

//    private String orderNumber;
//    private Long orderId;
    private Order order;
    private Long orderStateId;
    private OrderCurrentState orderCurrentState;

    @Builder
    public OrderStateResponse(Order order, Long orderStateId, OrderCurrentState orderCurrentState) {
        this.order = order;
        this.orderStateId = orderStateId;
        this.orderCurrentState = orderCurrentState;
    }

    @QueryProjection
    @Builder
    public OrderStateResponse(Order order, OrderState orderState) {
        this.order = order;
        this.orderStateId = orderState.getId();
        this.orderCurrentState = orderState.getOrderCurrentState();
    }
}
