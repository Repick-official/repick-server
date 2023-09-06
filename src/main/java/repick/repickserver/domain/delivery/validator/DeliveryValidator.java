package repick.repickserver.domain.delivery.validator;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import repick.repickserver.domain.delivery.domain.Delivery;
import repick.repickserver.domain.ordernumber.dao.OrderNumberReository;
import repick.repickserver.global.error.exception.CustomException;

import static repick.repickserver.global.error.exception.ErrorCode.ORDER_NOT_FOUND;
import static repick.repickserver.global.error.exception.ErrorCode.WAYBILL_NUMBER_NOT_REGISTERED;

@Service @RequiredArgsConstructor
public class DeliveryValidator {

    private final OrderNumberReository orderNumberReository;

    public void validateOrderNumber(String orderNumber) {
        // 주문번호가 유효하지 않는 경우 예외처리
        if (!orderNumberReository.existsByOrderNumber(orderNumber)) {
            throw new CustomException(ORDER_NOT_FOUND);
        }
    }

    public void validateWaybillNumber(Delivery delivery) {
        // 운송장번호가 유효하지 않는 경우 예외처리
        if (delivery == null) throw new CustomException(WAYBILL_NUMBER_NOT_REGISTERED);
    }
}
