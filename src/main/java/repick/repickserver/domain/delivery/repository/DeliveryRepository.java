package repick.repickserver.domain.delivery.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import repick.repickserver.domain.delivery.domain.Delivery;

public interface DeliveryRepository extends JpaRepository<Delivery, Long> {

    Delivery findByOrderNumber(String orderNumber);
}
