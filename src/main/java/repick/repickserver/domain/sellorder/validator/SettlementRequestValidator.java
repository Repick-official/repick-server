package repick.repickserver.domain.sellorder.validator;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import repick.repickserver.domain.member.domain.Member;
import repick.repickserver.domain.product.domain.Product;
import repick.repickserver.domain.product.domain.ProductState;
import repick.repickserver.domain.sellorder.dto.SettlementRequest;
import repick.repickserver.global.error.exception.CustomException;

import static repick.repickserver.global.error.exception.ErrorCode.*;

@Component @RequiredArgsConstructor
public class SettlementRequestValidator {

    public void validateSettlementRequest(SettlementRequest settlementRequest) {
        // productIds를 null로 보낸 경우
        if (settlementRequest.getProductIds() == null) throw new CustomException(INVALID_REQUEST_ERROR);
        // productIds를 빈 리스트로 보낸 경우
        if (settlementRequest.getProductIds().size() == 0) throw new CustomException(INVALID_REQUEST_ERROR);
    }

    public void validateSettlementRequestByMemberAndProductList(Member member, Product product) {
        if (!product.getSellOrder().getMember().getId().equals(member.getId()))
            throw new CustomException(ORDER_MEMBER_NOT_MATCH);
    }

    public void validateSettlementRequestIsSoldOut(Product product) {
        if (product.getProductState() != ProductState.SOLD_OUT)
            throw new CustomException(PRODUCT_NOT_SOLD_OUT);
    }
}
