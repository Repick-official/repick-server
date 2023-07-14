package repick.repickserver.domain.ordernumber.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import repick.repickserver.domain.ordernumber.domain.OrderNumber;

public interface OrderNumberReository extends JpaRepository<OrderNumber, Long> {
    boolean existsByOrderNumber(String orderNumber);
}
