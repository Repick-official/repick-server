package repick.repickserver.domain.cart.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import repick.repickserver.domain.cart.domain.OrderProduct;

import java.util.List;

public interface OrderProductRepository extends JpaRepository<OrderProduct, Long> {
    List<OrderProduct> findByOrderId(Long orderId);
}
