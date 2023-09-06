package repick.repickserver.domain.order.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import repick.repickserver.domain.order.domain.OrderState;

public interface OrderStateRepository extends JpaRepository<OrderState, Long>, OrderStateRepositoryCustom {
}
