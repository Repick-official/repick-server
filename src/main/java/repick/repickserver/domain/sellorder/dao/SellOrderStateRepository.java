package repick.repickserver.domain.sellorder.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import repick.repickserver.domain.sellorder.domain.SellOrderState;

public interface SellOrderStateRepository extends JpaRepository<SellOrderState, Long>, SellOrderStateRepositoryCustom {
}
