package repick.repickserver.domain.cart.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import repick.repickserver.domain.cart.domain.CartProduct;
import repick.repickserver.domain.cart.domain.CartProductState;

import java.util.List;
import java.util.Optional;

public interface CartProductRepository extends JpaRepository<CartProduct, Long> {
    Boolean existsByProductIdAndCartId(Long productId, Long cartId);
    Optional<CartProduct> findByCartIdAndProductId(Long cartId, Long productId);
}
