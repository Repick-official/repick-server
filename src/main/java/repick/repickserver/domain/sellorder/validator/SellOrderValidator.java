package repick.repickserver.domain.sellorder.validator;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import repick.repickserver.domain.sellorder.domain.SellOrder;
import repick.repickserver.domain.sellorder.dto.SellOrderRequest;
import repick.repickserver.domain.sellorder.repository.SellOrderRepository;
import repick.repickserver.global.error.exception.CustomException;

import static repick.repickserver.domain.sellorder.domain.SellState.BAG_PENDING;
import static repick.repickserver.global.error.exception.ErrorCode.ORDER_NOT_FOUND;

@Component @RequiredArgsConstructor
public class SellOrderValidator {

    private final SellOrderRepository sellOrderRepository;

    public void validateSellOrder(SellOrderRequest request) {
        SellOrderRequest.validateSellOrder(request);
    }

    public void validateSellOrderMatchesMemberId(SellOrder sellOrder, Long memberId) {
        if (!sellOrder.getMember().getId().equals(memberId)) throw new CustomException(ORDER_NOT_FOUND);
    }

    public void validateIsSellOrderStateBagPending(SellOrder sellOrder) {
        if (!sellOrderRepository.existsBySellOrderIdAndSellState(sellOrder.getId(), BAG_PENDING)) {
            throw new CustomException(ORDER_NOT_FOUND);
        }
    }
}
