package repick.repickserver.domain.subscriberinfo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import repick.repickserver.domain.subscriberinfo.domain.SubscribeState;
import repick.repickserver.domain.subscriberinfo.domain.SubscriberInfo;

import javax.transaction.Transactional;

@Transactional
public interface SubscriberInfoRepository extends JpaRepository<SubscriberInfo, Long>, SubscriberInfoRepositoryCustom {
    SubscriberInfo findByOrderNumberAndSubscribeState(String orderNumber, SubscribeState subscribeState);
}
