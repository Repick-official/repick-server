package repick.repickserver.domain.cart.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import repick.repickserver.domain.cart.domain.Order;
import repick.repickserver.domain.cart.domain.OrderProduct;
import repick.repickserver.domain.cart.domain.OrderState;
import repick.repickserver.domain.cart.dto.OrderRequest;
import repick.repickserver.domain.cart.dto.OrderResponse;
import repick.repickserver.domain.cart.dto.OrderStateResponse;
import repick.repickserver.domain.member.domain.Member;

import java.util.List;

import static repick.repickserver.domain.cart.domain.OrderCurrentState.UNPAID;

@Service @RequiredArgsConstructor
public class OrderMapper {

    public OrderState toOrderState(Order order) {
        return OrderState.builder()
                .order(order)
                .orderCurrentState(UNPAID)
                .build();
    }

    public Order toOrder(Member member, OrderRequest orderRequest, String orderNumber) {
        return Order.builder()
                .member(member)
                .personName(orderRequest.getPersonName())
                .phoneNumber(orderRequest.getPhoneNumber())
                .address(orderRequest.getAddress())
                .requestDetail(orderRequest.getRequestDetail())
                .orderNumber(orderNumber)
                .build();

    }

    public OrderResponse toOrderResponse(Order order, OrderState orderState, List<OrderProduct> orderProducts) {
        return OrderResponse.builder()
                .order(order)
                .orderState(orderState)
                .orderProducts(orderProducts)
                .build();
    }

    public OrderStateResponse toOrderStateResponse(Order order, OrderState orderState) {
        return OrderStateResponse.builder()
                .order(order)
                .orderStateId(orderState.getId())
                .orderCurrentState(orderState.getOrderCurrentState())
                .build();
    }

}
