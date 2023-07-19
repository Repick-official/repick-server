package repick.repickserver.domain.cart.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import repick.repickserver.domain.cart.domain.CartProduct;
import repick.repickserver.domain.cart.domain.CartProductState;

import java.util.List;

public interface CartProductRepository extends JpaRepository<CartProduct, Long> {

    List<CartProduct> findByCartIdAndCartProductState(Long cartId, CartProductState cartProductState);
    Boolean existsByProductIdAndCartId(Long productId, Long cartId);

}
