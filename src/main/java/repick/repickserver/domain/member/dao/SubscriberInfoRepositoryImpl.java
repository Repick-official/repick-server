package repick.repickserver.domain.member.dao;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import repick.repickserver.domain.member.domain.SubscribeState;
import repick.repickserver.domain.member.domain.SubscriberInfo;

import java.time.LocalDateTime;
import java.util.List;

import static repick.repickserver.domain.member.domain.QSubscriberInfo.subscriberInfo;

@RequiredArgsConstructor
@Repository
public class SubscriberInfoRepositoryImpl implements SubscriberInfoRepositoryCustom {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public List<SubscriberInfo> findValidSubscriberInfo(Long id) {

        return jpaQueryFactory.selectFrom(subscriberInfo)
                // 멤버 id 일치하는 것들로 필터
                .where(subscriberInfo.member.id.eq(id)
                // 승인된 것들로 필터
                .and(subscriberInfo.subscribeState.eq(SubscribeState.APPROVED))
                // 구독 만료일이 현재 날짜보다 미래인 경우
                .and(subscriberInfo.expireDate.after(LocalDateTime.now())))
                .fetch();
    }

    @Override
    public List<SubscriberInfo> findValidRequests() {

        return jpaQueryFactory.selectFrom(subscriberInfo)
                // 승인 대기중인 것들로 필터
                .where(subscriberInfo.subscribeState.eq(SubscribeState.REQUESTED)
                // 처리되지 않은 것들로 필터
                .and(subscriberInfo.childSubscriberInfos.isEmpty())
                // 구독 만료일이 현재 날짜보다 미래인 경우
                .and(subscriberInfo.expireDate.after(LocalDateTime.now())))
                .fetch();
    }

    @Override
    public List<SubscriberInfo> findSubscriberInfo(Long id, SubscribeState subscribeState, boolean isExpired) {
        return jpaQueryFactory.selectFrom(subscriberInfo)
                // 멤버 id 일치하는 것들로 필터
                .where(subscriberInfo.member.id.eq(id)
                // 상태로 필터
                .and(subscriberInfo.subscribeState.eq(subscribeState))
                // 처리되지 않은, 즉 childSubscriberInfo가 null인 것들로 필터
                .and(subscriberInfo.childSubscriberInfos.isEmpty())
                // 만료여부 필터
                .and(isExpired ? subscriberInfo.expireDate.before(LocalDateTime.now())
                        : subscriberInfo.expireDate.after(LocalDateTime.now())))
                .fetch();
    }
}