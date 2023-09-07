package repick.repickserver.domain.sellorder.validator;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import repick.repickserver.domain.sellorder.domain.SellOrder;
import repick.repickserver.domain.sellorder.dto.SellOrderRequest;
import repick.repickserver.domain.sellorder.repository.SellOrderRepository;
import repick.repickserver.global.error.exception.CustomException;

import static repick.repickserver.global.error.exception.ErrorCode.ORDER_NOT_FOUND;
import static repick.repickserver.global.error.exception.ErrorCode.ORDER_NUMBER_NOT_FOUND;

@Component @RequiredArgsConstructor
public class SellOrderValidator {

    private final SellOrderRepository sellOrderRepository;

    public void validateSellOrder(SellOrderRequest request) {
        SellOrderRequest.validateSellOrder(request);
    }

    public void validateSellOrderByOrderNumber(String orderNumber) {
        if (orderNumber == null) throw new CustomException(ORDER_NUMBER_NOT_FOUND);
        SellOrder sellOrder = sellOrderRepository.findByOrderNumber(orderNumber)
                .orElseThrow(() -> new CustomException(ORDER_NOT_FOUND));
    }
}
