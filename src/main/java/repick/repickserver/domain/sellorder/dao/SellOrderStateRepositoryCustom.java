package repick.repickserver.domain.sellorder.dao;

import repick.repickserver.domain.sellorder.domain.SellOrderState;
import repick.repickserver.domain.sellorder.domain.SellState;

public interface SellOrderStateRepositoryCustom {
    SellOrderState findLastStateBySellOrderId(Long sellOrderId);
    boolean isLastBySellOrderId(Long sellOrderId, SellState state);
}
