package repick.repickserver.domain.order.converter;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import repick.repickserver.domain.order.domain.Order;
import repick.repickserver.domain.order.domain.OrderCurrentState;
import repick.repickserver.domain.order.domain.OrderProduct;
import repick.repickserver.domain.order.domain.OrderState;
import repick.repickserver.domain.order.dto.OrderRequest;
import repick.repickserver.domain.order.dto.OrderResponse;
import repick.repickserver.domain.order.dto.OrderStateResponse;
import repick.repickserver.domain.member.domain.Member;

import java.util.List;

@Service @RequiredArgsConstructor
public class OrderConverter {

    public OrderState toOrderState(Order order, OrderCurrentState orderCurrentState) {
        return OrderState.from(order, orderCurrentState);
    }

    public Order toOrder(Member member, OrderRequest orderRequest, String orderNumber) {
        return OrderRequest.toOrder(member, orderRequest, orderNumber);
    }

    public OrderResponse toOrderResponse(Order order, OrderState orderState, List<OrderProduct> orderProducts) {
        return OrderResponse.from(order, orderState, orderProducts);
    }

    public OrderStateResponse toOrderStateResponse(Order order, OrderState orderState) {
        return OrderStateResponse.from(order, orderState);
    }

}
