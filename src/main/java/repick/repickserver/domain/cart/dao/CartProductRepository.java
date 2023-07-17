package repick.repickserver.domain.cart.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import repick.repickserver.domain.cart.domain.CartProduct;

public interface CartProductRepository extends JpaRepository<CartProduct, Long> {
}
