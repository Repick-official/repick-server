package repick.repickserver.domain.order.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import repick.repickserver.domain.order.domain.Order;

public interface OrderRepository extends JpaRepository<Order, Long> {
}
