package repick.repickserver.domain.sellorder.repository;

import repick.repickserver.domain.sellorder.domain.SellOrder;
import repick.repickserver.domain.sellorder.domain.SellState;

import java.util.List;

public interface SellOrderRepositoryCustom {

    List<SellOrder> getSellOrdersById(Long id);

    List<SellOrder> getSellOrdersByMemberIdAndState(Long memberId, SellState state);

    List<SellOrder> getSellOrdersByState(SellState state);
}
