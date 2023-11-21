package repick.repickserver.domain.sellorder.repository;

import repick.repickserver.domain.sellorder.domain.SellState;
import repick.repickserver.domain.sellorder.dto.SellOrderResponse;

import java.util.List;

public interface SellOrderRepositoryCustom {

    List<SellOrderResponse> getSellOrderResponseById(Long id);

    List<SellOrderResponse> getSellOrdersByMemberIdAndState(Long memberId, SellState state);

    List<SellOrderResponse> getSellOrdersByState(SellState state);

    Long countBySellState(SellState requestedState);

    Boolean existsBySellOrderIdAndSellState(Long sellOrderId, SellState state);
}
