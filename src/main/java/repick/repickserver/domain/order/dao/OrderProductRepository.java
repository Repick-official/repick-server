package repick.repickserver.domain.order.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import repick.repickserver.domain.order.domain.OrderProduct;

import java.util.List;

public interface OrderProductRepository extends JpaRepository<OrderProduct, Long> {
    List<OrderProduct> findByOrderId(Long orderId);
}
