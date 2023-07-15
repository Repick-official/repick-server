package repick.repickserver.domain.member.dao;

import repick.repickserver.domain.member.domain.SubscribeState;
import repick.repickserver.domain.member.domain.SubscriberInfo;

import java.util.List;

public interface SubscriberInfoRepositoryCustom {


    SubscriberInfo findValidSubscriberInfo(Long id);

    List<SubscriberInfo> findValidRequests();

    List<SubscriberInfo> findSubscriberInfo(Long id, SubscribeState subscribeState, boolean isExpired);
}
