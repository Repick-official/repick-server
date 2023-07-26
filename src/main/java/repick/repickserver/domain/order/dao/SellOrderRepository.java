package repick.repickserver.domain.order.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import repick.repickserver.domain.order.domain.SellOrder;

import java.util.Optional;

public interface SellOrderRepository extends JpaRepository<SellOrder, Long>, SellOrderRepositoryCustom {
    Optional<SellOrder> findByOrderNumber(String orderNumber);
}
