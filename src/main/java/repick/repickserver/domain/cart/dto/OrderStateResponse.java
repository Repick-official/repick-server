package repick.repickserver.domain.cart.dto;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import repick.repickserver.domain.cart.domain.OrderCurrentState;

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
}
