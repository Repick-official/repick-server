package repick.repickserver.domain.homefitting.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import repick.repickserver.domain.homefitting.domain.HomeFitting;

import java.util.List;

public interface HomeFittingRepository extends JpaRepository<HomeFitting, Long>, HomeFittingRepositoryCustom {
    HomeFitting findByCartProductId(Long cartProductId);
    List<HomeFitting> findByOrderNumber(String orderNumber);
}
