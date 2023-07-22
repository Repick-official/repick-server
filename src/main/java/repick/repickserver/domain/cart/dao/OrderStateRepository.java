package repick.repickserver.domain.cart.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import repick.repickserver.domain.cart.domain.OrderState;
import repick.repickserver.domain.cart.dto.OrderStateResponse;

import java.util.Optional;

public interface OrderStateRepository extends JpaRepository<OrderState, Long>, OrderStateRepositoryCustom {
}
