package repick.repickserver.domain.cart.dao;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import repick.repickserver.domain.cart.domain.HomeFitting;

import java.util.Optional;

import static repick.repickserver.domain.cart.domain.QCartProduct.cartProduct;
import static repick.repickserver.domain.cart.domain.QHomeFitting.homeFitting;

@RequiredArgsConstructor
public class HomeFittingRepositoryImpl implements HomeFittingRepositoryCustom {

    private final JPAQueryFactory jpaQueryFactory;

    public Optional<HomeFitting> findHomeFittingByCartIdAndProductId(Long cartId, Long productId) {
        return Optional.ofNullable(jpaQueryFactory
                .select(homeFitting)
                .from(homeFitting)
                .leftJoin(cartProduct)
                .on(homeFitting.cartProduct.id.eq(cartProduct.id))
                .where(cartProduct.cart.id.eq(cartId)
                        .and(cartProduct.product.id.eq(productId)))
                .fetchOne());
    }
}
