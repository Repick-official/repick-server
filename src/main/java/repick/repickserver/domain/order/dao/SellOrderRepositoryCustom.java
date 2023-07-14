package repick.repickserver.domain.order.dao;

import repick.repickserver.domain.order.domain.SellOrder;
import repick.repickserver.domain.order.domain.SellState;

import java.util.List;

public interface SellOrderRepositoryCustom {

    List<SellOrder> getSellOrdersById(Long id);

    List<SellOrder> getSellOrdersByIdAndState(Long id, SellState state);

    List<SellOrder> getSellOrdersByState(SellState state);
}
