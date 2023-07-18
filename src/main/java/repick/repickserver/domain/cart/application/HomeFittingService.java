package repick.repickserver.domain.cart.application;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import repick.repickserver.domain.cart.dao.CartProductRepository;
import repick.repickserver.domain.cart.dao.HomeFittingRepository;
import repick.repickserver.domain.cart.domain.CartProduct;
import static repick.repickserver.domain.cart.domain.CartProductState.*;

import repick.repickserver.domain.cart.domain.HomeFitting;
import repick.repickserver.domain.cart.dto.HomeFittingResponse;
import repick.repickserver.domain.product.dao.ProductRepository;
import repick.repickserver.domain.product.domain.Product;
import repick.repickserver.global.error.exception.CustomException;
import repick.repickserver.global.jwt.JwtProvider;
import static repick.repickserver.domain.product.domain.ProductState.DELETED;
import static repick.repickserver.domain.product.domain.ProductState.SOLD_OUT;
import static repick.repickserver.global.error.exception.ErrorCode.*;

@Service
@Transactional
@RequiredArgsConstructor
public class HomeFittingService {

    private final HomeFittingRepository homeFittingRepository;
    private final CartProductRepository cartProductRepository;
    private final ProductRepository productRepository;

    public HomeFittingResponse requestHomeFitting(Long cartProductId) {

        CartProduct cartProduct = cartProductRepository.findById(cartProductId)
                .orElseThrow(() -> new CustomException(INVALID_CART_PRODUCT_ID));

        Product product = productRepository.findById(cartProduct.getProduct().getId())
                .orElseThrow(() -> new CustomException(PRODUCT_NOT_FOUND));
        // 상품이 품절된 경우
        if(product.getProductState().equals(SOLD_OUT)) {
            throw new CustomException(PRODUCT_SOLD_OUT);
        }
        // 판매하지 않는 상품인 경우
        else if(product.getProductState().equals(DELETED)) {
            throw new CustomException(PRODUCT_NOT_FOUND);
        }

        // 마이픽에 담긴 상품인지 확인
        if(cartProduct.getCartProductState().equals(IN_CART)) {
            cartProduct.changeState(HOME_FITTING_REQUESTED);
        }
        else {
            throw new CustomException(INVALID_CART_PRODUCT_STATE);
        }

        return HomeFittingResponse.builder()
                .homeFitting(homeFittingRepository.save(
                        HomeFitting.builder()
                                .cartProduct(cartProduct)
                                .build()))
                .build();
    }
}
