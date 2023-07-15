package repick.repickserver.domain.member.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import repick.repickserver.domain.member.domain.SubscribeState;
import repick.repickserver.domain.member.domain.SubscriberInfo;

import javax.transaction.Transactional;

@Transactional
public interface SubscriberInfoRepository extends JpaRepository<SubscriberInfo, Long>, SubscriberInfoRepositoryCustom {
    SubscriberInfo findByOrderNumberAndSubscribeState(String orderNumber, SubscribeState subscribeState);
}
