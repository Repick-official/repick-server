package repick.repickserver.domain.order.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import repick.repickserver.domain.order.domain.SellInfo;

public interface SellInfoRepository extends JpaRepository<SellInfo, Long> {
}
