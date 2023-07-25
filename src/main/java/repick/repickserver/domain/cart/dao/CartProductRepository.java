package repick.repickserver.domain.cart.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import repick.repickserver.domain.cart.domain.CartProduct;
import repick.repickserver.domain.cart.domain.CartProductState;

import java.util.Optional;

public interface CartProductRepository extends JpaRepository<CartProduct, Long>, CartProductRepositoryCustom {
    Optional<CartProduct> findByIdAndCartProductState(Long id, CartProductState cartProductState);
}
