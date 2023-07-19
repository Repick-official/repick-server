package repick.repickserver.domain.cart.application;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import repick.repickserver.domain.cart.dao.CartProductRepository;
import repick.repickserver.domain.cart.dao.CartRepository;
import repick.repickserver.domain.cart.domain.Cart;
import repick.repickserver.domain.cart.domain.CartProduct;
import repick.repickserver.domain.cart.dto.GetMyPickResponse;
import repick.repickserver.domain.cart.dto.MyPickResponse;
import repick.repickserver.domain.member.domain.Member;
import repick.repickserver.domain.ordernumber.application.OrderNumberService;
import repick.repickserver.domain.product.dao.ProductRepository;
import repick.repickserver.domain.product.domain.Product;
import repick.repickserver.global.error.exception.CustomException;
import repick.repickserver.global.jwt.JwtProvider;
import java.util.List;
import static repick.repickserver.global.error.exception.ErrorCode.*;
import static repick.repickserver.domain.product.domain.ProductState.*;

@Service
@Transactional
@RequiredArgsConstructor
public class CartService {

    private final CartRepository cartRepository;
    private final CartProductRepository cartProductRepository;
    private final ProductRepository productRepository;
    private final JwtProvider jwtProvider;

    public MyPickResponse createMyPick(Long productId, String token) {

        Member member = jwtProvider.getMemberByRawToken(token);
        Cart cart = cartRepository.findByMember(member);

        Product product = productRepository.findByIdAndProductState(productId, SELLING)
                .orElseThrow(() -> new CustomException(PRODUCT_NOT_SELLING));

        // 이미 장바구니에 담겨 있는 경우
        if (cartProductRepository.existsByProductIdAndCartId(productId, cart.getId()))
            throw new CustomException(PRODUCT_ALREADY_EXIST_IN_CART);

        CartProduct savedCartProduct = cartProductRepository.save(
                CartProduct.builder()
                        .product(product)
                        .cart(cart)
                        .build()
        );

        return MyPickResponse.builder()
                .memberId(member.getId())
                .cartId(savedCartProduct.getCart().getId())
                .productId(savedCartProduct.getProduct().getId())
                .cartProductState(savedCartProduct.getCartProductState())
                .cartProductId(savedCartProduct.getId())
                .build();
    }

    public List<GetMyPickResponse> getMyPick(String token) {
        Member member = jwtProvider.getMemberByRawToken(token);
        Cart cart = cartRepository.findByMember(member);

        return productRepository.getMyPickProducts(cart.getId());
    }
}
