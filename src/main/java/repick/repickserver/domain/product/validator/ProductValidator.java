package repick.repickserver.domain.product.validator;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import repick.repickserver.domain.product.domain.Product;
import repick.repickserver.global.error.exception.CustomException;

import java.util.List;

import static repick.repickserver.domain.product.domain.ProductState.SETTLEMENT_REQUESTED;
import static repick.repickserver.global.error.exception.ErrorCode.*;

@Component @RequiredArgsConstructor
public class ProductValidator {

    public void validateProductListByProductIds(List<Product> productList, List<Long> productIds) {
        if (productList.size() != productIds.size()) {
            throw new CustomException(ORDER_MEMBER_NOT_MATCH);
        }
    }

    public void validateProductIsSettlementRequested(Product product) {
        if (product.getProductState() != SETTLEMENT_REQUESTED)
            throw new CustomException(PRODUCT_NOT_SETTLEMENT_REQUESTED);
    }

    public void validateProductByMemberId(Product product, Long memberId) {
        if (!product.getSellOrder().getMember().getId().equals(memberId)) {
            throw new CustomException(ORDER_MEMBER_NOT_MATCH);
        }
    }

    public void validatePrice(Long price) {
        if (price % 1000 != 0) {
            throw new CustomException(INVALID_PRICE);
        }
    }
}
