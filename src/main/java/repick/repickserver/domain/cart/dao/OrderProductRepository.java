package repick.repickserver.domain.cart.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import repick.repickserver.domain.cart.domain.OrderProduct;

public interface OrderProductRepository extends JpaRepository<OrderProduct, Long> {
}
