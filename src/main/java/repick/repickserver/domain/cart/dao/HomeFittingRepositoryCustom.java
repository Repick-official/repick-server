package repick.repickserver.domain.cart.dao;

import repick.repickserver.domain.cart.domain.HomeFitting;

import java.util.Optional;

public interface HomeFittingRepositoryCustom {
    Optional<HomeFitting> findHomeFittingByCartIdAndProductId(Long cartId, Long productId);

    Boolean existsByMemberAndProduct(Long memberId, Long productId);
}
