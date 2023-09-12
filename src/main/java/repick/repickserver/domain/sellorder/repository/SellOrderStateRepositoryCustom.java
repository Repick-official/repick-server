package repick.repickserver.domain.sellorder.repository;

import repick.repickserver.domain.sellorder.domain.SellOrderState;
import repick.repickserver.domain.sellorder.domain.SellState;

public interface SellOrderStateRepositoryCustom {
    SellOrderState findLastStateBySellOrderId(Long sellOrderId);
    boolean isLastBySellOrderId(Long sellOrderId, SellState state);
}
