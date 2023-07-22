package repick.repickserver.domain.cart.dao;

public interface CartProductRepositoryCustom {

    Boolean existsByCartIdAndProductIdAndStateIsNotDeleted(Long cartId, Long productId);

}
