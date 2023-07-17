package repick.repickserver.domain.cart.dto;

import com.querydsl.core.annotations.QueryProjection;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import repick.repickserver.domain.cart.domain.CartProductState;
import repick.repickserver.domain.product.dto.GetProductResponse;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class GetMyPickResponse {

    private GetProductResponse product;
    private CartProductState cartProductState;

    @QueryProjection
    @Builder
    public GetMyPickResponse(GetProductResponse product, CartProductState cartProductState) {
        this.product = product;
        this.cartProductState = cartProductState;
    }
}
