package repick.repickserver.domain.subscriberinfo.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import repick.repickserver.domain.member.domain.Member;
import repick.repickserver.domain.subscriberinfo.domain.SubscribeState;
import repick.repickserver.domain.subscriberinfo.domain.SubscriberInfo;
import repick.repickserver.domain.subscriberinfo.dto.SubscriberInfoRegisterRequest;
import repick.repickserver.domain.ordernumber.application.OrderNumberService;
import repick.repickserver.domain.ordernumber.domain.OrderType;

import java.time.LocalDateTime;

@Service @RequiredArgsConstructor
public class SubscriberInfoMapper {

    private final OrderNumberService orderNumberService;

    public SubscriberInfo toSubscriberInfo(SubscriberInfoRegisterRequest request, Member member) {
        return SubscriberInfo.builder()
                .member(member) // 요청회원정보
                .orderNumber(orderNumberService.generateOrderNumber(OrderType.SUBSCRIBE)) // 주문번호 생성
                .expireDate(LocalDateTime.now().plusDays(7)) // 무통장입금의 경우 입금대기기간 1주일로 임의로 잡았음
                .subscribeState(SubscribeState.REQUESTED) // 상태는 요청
                .subscribeType(request.getSubscribeType()) // 구독타입
                .build();
    }

    public SubscriberInfo toSubscriberInfo(SubscriberInfo parent, SubscribeState newState, LocalDateTime expireDate) {
        return SubscriberInfo.builder()
                .member(parent.getMember())
                .orderNumber(parent.getOrderNumber())
                .parentSubscriberInfo(parent)
                .expireDate(expireDate)
                .subscribeState(newState)
                .subscribeType(parent.getSubscribeType())
                .build();
    }

}
