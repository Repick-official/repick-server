package repick.repickserver.domain.homefitting.dao;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import repick.repickserver.domain.homefitting.domain.HomeFitting;

import java.util.Optional;

import static repick.repickserver.domain.cart.domain.QCartProduct.cartProduct;
import static repick.repickserver.domain.homefitting.domain.QHomeFitting.homeFitting;

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

    @Override
    public Boolean existsByMemberAndProduct(Long memberId, Long productId) {
        return jpaQueryFactory.select(homeFitting)
                .from(homeFitting)
                .leftJoin(cartProduct)
                .on(homeFitting.cartProduct.id.eq(cartProduct.id))
                .where(cartProduct.cart.member.id.eq(memberId)
                        .and(cartProduct.product.id.eq(productId)))
                .fetchOne() != null;
    }
}
