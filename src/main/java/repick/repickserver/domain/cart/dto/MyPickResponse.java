package repick.repickserver.domain.cart.dto;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import repick.repickserver.domain.cart.domain.CartProductState;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MyPickResponse {

    private Long memberId;
    private Long cartId;
    private Long productId;
    private CartProductState cartProductState;
    private Long cartProductId;

    @Builder
    public MyPickResponse(Long memberId, Long cartId, Long productId, CartProductState cartProductState, Long cartProductId) {
        this.memberId = memberId;
        this.cartId = cartId;
        this.productId = productId;
        this.cartProductState = cartProductState;
        this.cartProductId = cartProductId;
    }
}
