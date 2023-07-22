package repick.repickserver.domain.cart.dao;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import repick.repickserver.domain.cart.domain.CartProductState;
import repick.repickserver.domain.cart.domain.QCartProduct;

@Repository
@RequiredArgsConstructor
public class CartProductRepositoryImpl implements CartProductRepositoryCustom {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public Boolean existsByCartIdAndProductIdAndStateIsNotDeleted(Long cartId, Long productId) {
        return jpaQueryFactory.selectFrom(QCartProduct.cartProduct)
                .where(QCartProduct.cartProduct.product.id.eq(productId)
                        .and(QCartProduct.cartProduct.cart.id.eq(cartId))
                        .and(QCartProduct.cartProduct.cartProductState.ne(CartProductState.DELETED)))
                .fetchOne() != null;
    }
}
