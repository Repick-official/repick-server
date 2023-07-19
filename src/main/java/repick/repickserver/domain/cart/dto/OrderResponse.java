package repick.repickserver.domain.cart.dto;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import repick.repickserver.domain.cart.domain.Order;
import repick.repickserver.domain.cart.domain.OrderCurrentState;
import repick.repickserver.domain.cart.domain.OrderState;
import repick.repickserver.domain.model.Address;

import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OrderResponse {

    private Long id;

    private Long memberId;

    private String personName;

    private String phoneNumber;

    private Address address;

    private String requestDetail;

    private List<OrderState> orderState;

    private String orderNumber;

    @Builder
    public OrderResponse(Order order) {
        this.id = order.getId();
        this.memberId = order.getMember().getId();
        this.personName = order.getPersonName();
        this.phoneNumber = order.getPhoneNumber();
        this.address = order.getAddress();
        this.requestDetail = order.getRequestDetail();
        this.orderState = order.getOrderState();
        this.orderNumber = order.getOrderNumber();
    }
}
