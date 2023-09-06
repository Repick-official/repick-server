package repick.repickserver.domain.sellorder.validator;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import repick.repickserver.domain.sellorder.dao.SellOrderRepository;
import repick.repickserver.domain.sellorder.domain.SellOrder;
import repick.repickserver.domain.sellorder.dto.SellOrderRequest;
import repick.repickserver.global.error.exception.CustomException;

import static repick.repickserver.global.error.exception.ErrorCode.*;

@Service @RequiredArgsConstructor
public class SellOrderValidator {

    private final SellOrderRepository sellOrderRepository;

    public void validateSellOrder(SellOrderRequest request) {
        if (request.getName() == null) throw new CustomException(ORDER_NAME_NOT_FOUND);
        if (request.getAddress() == null
        || request.getAddress().getMainAddress() == null
        || request.getAddress().getDetailAddress() == null
        || request.getAddress().getZipCode() == null) throw new CustomException(ORDER_ADDRESS_NOT_FOUND);
        if (request.getBagQuantity() == null) throw new CustomException(ORDER_BAG_QUANTITY_NOT_FOUND);
        if (request.getProductQuantity() == null) throw new CustomException(ORDER_PRODUCT_QUANTITY_NOT_FOUND);
        if (request.getPhoneNumber() == null) throw new CustomException(ORDER_PHONE_NUMBER_NOT_FOUND);
    }

    public void validateSellOrderByOrderNumber(String orderNumber) {
        if (orderNumber == null) throw new CustomException(ORDER_NUMBER_NOT_FOUND);
        SellOrder sellOrder = sellOrderRepository.findByOrderNumber(orderNumber)
                .orElseThrow(() -> new CustomException(ORDER_NOT_FOUND));
    }
}
