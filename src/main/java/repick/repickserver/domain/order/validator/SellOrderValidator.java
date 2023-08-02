package repick.repickserver.domain.order.validator;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import repick.repickserver.domain.order.dto.SellOrderRequest;
import repick.repickserver.global.error.exception.CustomException;

import static repick.repickserver.global.error.exception.ErrorCode.*;

@Service @RequiredArgsConstructor
public class SellOrderValidator {

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
}
