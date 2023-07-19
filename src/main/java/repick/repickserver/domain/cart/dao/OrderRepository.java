package repick.repickserver.domain.cart.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import repick.repickserver.domain.cart.domain.Order;

public interface OrderRepository extends JpaRepository<Order, Long> {
}
