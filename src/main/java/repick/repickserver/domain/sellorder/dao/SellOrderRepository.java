package repick.repickserver.domain.sellorder.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import repick.repickserver.domain.sellorder.domain.SellOrder;

import java.util.Optional;

public interface SellOrderRepository extends JpaRepository<SellOrder, Long>, SellOrderRepositoryCustom {
    Optional<SellOrder> findByOrderNumber(String orderNumber);
}
