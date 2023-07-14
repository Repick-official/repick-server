package repick.repickserver.domain.order.dao;

import repick.repickserver.domain.order.domain.SellOrderState;
import repick.repickserver.domain.order.domain.SellState;

public interface SellOrderStateRepositoryCustom {
    SellOrderState findLastStateBySellOrderId(Long sellOrderId);
    boolean isLastBySellOrderId(Long sellOrderId, SellState state);
}
