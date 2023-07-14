package repick.repickserver.domain.order.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import repick.repickserver.domain.order.domain.SellOrderState;

public interface SellOrderStateRepository extends JpaRepository<SellOrderState, Long>, SellOrderStateRepositoryCustom {
}
