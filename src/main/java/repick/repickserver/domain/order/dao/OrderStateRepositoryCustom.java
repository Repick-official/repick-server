package repick.repickserver.domain.order.dao;

import repick.repickserver.domain.order.dto.OrderStateResponse;

import java.util.List;

public interface OrderStateRepositoryCustom {
    List<OrderStateResponse> getOrderStates(String requestedOrderState);

    Long countByOrderCurrentState(String requestedState);
}
