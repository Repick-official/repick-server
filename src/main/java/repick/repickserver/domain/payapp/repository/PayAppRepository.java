package repick.repickserver.domain.payapp.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import repick.repickserver.domain.payapp.domain.PayApp;

import java.util.Optional;

public interface PayAppRepository extends JpaRepository<PayApp, Long> {
    Optional<PayApp> findByPrice(Long price);
}
