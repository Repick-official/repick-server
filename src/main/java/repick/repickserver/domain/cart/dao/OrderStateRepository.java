package repick.repickserver.domain.cart.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import repick.repickserver.domain.cart.domain.OrderState;

public interface OrderStateRepository extends JpaRepository<OrderState, Long> {
}
