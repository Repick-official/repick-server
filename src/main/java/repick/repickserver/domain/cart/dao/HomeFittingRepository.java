package repick.repickserver.domain.cart.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import repick.repickserver.domain.cart.domain.CartProduct;
import repick.repickserver.domain.cart.domain.HomeFitting;
import repick.repickserver.domain.cart.domain.HomeFittingState;

import java.util.List;

public interface HomeFittingRepository extends JpaRepository<HomeFitting, Long>, HomeFittingRepositoryCustom {
    HomeFitting findByCartProductId(Long cartProductId);
    List<HomeFitting> findByOrderNumber(String orderNumber);
}
