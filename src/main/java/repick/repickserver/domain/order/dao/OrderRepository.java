package repick.repickserver.domain.order.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import repick.repickserver.domain.order.domain.Order;

import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long> {
    Optional<Order> findByOrderNumber(String orderNumber);
}
