package repick.repickserver.domain.sellorder.dao;

import repick.repickserver.domain.sellorder.domain.SellOrder;
import repick.repickserver.domain.sellorder.domain.SellState;

import java.util.List;

public interface SellOrderRepositoryCustom {

    List<SellOrder> getSellOrdersById(Long id);

    List<SellOrder> getSellOrdersByIdAndState(Long id, SellState state);

    List<SellOrder> getSellOrdersByState(SellState state);
}
