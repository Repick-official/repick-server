package repick.repickserver.domain.cart.dao;

import repick.repickserver.domain.cart.domain.CartProduct;

import java.util.Optional;

public interface CartProductRepositoryCustom {

    Boolean existsByCartIdAndProductIdAndIsNotDeleted(Long cartId, Long productId);
    Optional<CartProduct> findByCartIdAndProductIdAndIsNotDeleted(Long cartId, Long productId);

}
