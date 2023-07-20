package repick.repickserver.domain.cart.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import repick.repickserver.domain.cart.domain.OrderState;

import java.util.Optional;

public interface OrderStateRepository extends JpaRepository<OrderState, Long> {
    Optional<OrderState> findByOrderId(Long orderId);
}
