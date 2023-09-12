package repick.repickserver.domain.sellorder.validator;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import repick.repickserver.domain.product.domain.Product;
import repick.repickserver.domain.product.domain.ProductState;
import repick.repickserver.domain.sellorder.dto.SettlementRequest;
import repick.repickserver.global.error.exception.CustomException;

import static repick.repickserver.global.error.exception.ErrorCode.*;

@Component @RequiredArgsConstructor
public class SettlementRequestValidator {

    public void validateSettlementRequest(SettlementRequest settlementRequest) {
        if (settlementRequest.getProductIds() == null) throw new CustomException(INVALID_REQUEST_ERROR);
        if (settlementRequest.getProductIds().size() == 0) throw new CustomException(INVALID_REQUEST_ERROR);
    }

    public void validateSettlementRequestIsSoldOut(Product product) {
        if (product.getProductState() != ProductState.SOLD_OUT)
            throw new CustomException(PRODUCT_NOT_SOLD_OUT);
    }
}
