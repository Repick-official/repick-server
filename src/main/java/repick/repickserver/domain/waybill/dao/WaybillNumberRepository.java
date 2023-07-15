package repick.repickserver.domain.waybill.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import repick.repickserver.domain.waybill.domain.WaybillNumber;

public interface WaybillNumberRepository extends JpaRepository<WaybillNumber, Long> {

    WaybillNumber findByOrderNumber(String orderNumber);
}
