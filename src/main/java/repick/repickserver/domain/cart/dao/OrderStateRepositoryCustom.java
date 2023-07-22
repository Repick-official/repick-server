package repick.repickserver.domain.cart.dao;

import repick.repickserver.domain.cart.dto.OrderStateResponse;

import java.util.List;
import java.util.Optional;

public interface OrderStateRepositoryCustom {
    List<OrderStateResponse> getOrderStates(String requestedOrderState);
}
