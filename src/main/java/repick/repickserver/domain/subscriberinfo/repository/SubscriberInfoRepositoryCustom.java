package repick.repickserver.domain.subscriberinfo.repository;

import repick.repickserver.domain.subscriberinfo.domain.SubscribeState;
import repick.repickserver.domain.subscriberinfo.domain.SubscriberInfo;

import java.util.List;
import java.util.Optional;

public interface SubscriberInfoRepositoryCustom {


    Optional<SubscriberInfo> findValidSubscriberInfo(Long id);

    List<SubscriberInfo> findValidRequests();

    List<SubscriberInfo> findSubscriberInfoByMemberIdAndState(Long id, SubscribeState subscribeState, boolean isExpired);

    List<SubscriberInfo> findSubscriberInfoByMemberId(Long memberId);
}
