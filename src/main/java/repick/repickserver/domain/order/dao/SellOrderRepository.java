package repick.repickserver.domain.order.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import repick.repickserver.domain.order.domain.SellOrder;

import java.util.List;

public interface SellOrderRepository extends JpaRepository<SellOrder, Long>, SellOrderRepositoryCustom {
    List<SellOrder> findByOrderNumber(String orderNumber);
}
