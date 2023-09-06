package repick.repickserver.domain.homefitting.dao;

import repick.repickserver.domain.homefitting.domain.HomeFitting;

import java.util.Optional;

public interface HomeFittingRepositoryCustom {
    Optional<HomeFitting> findHomeFittingByCartIdAndProductId(Long cartId, Long productId);

    Boolean existsByMemberAndProduct(Long memberId, Long productId);
}
