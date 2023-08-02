package repick.repickserver.domain.member.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import repick.repickserver.domain.member.domain.Member;
import repick.repickserver.domain.member.domain.SubscribeState;
import repick.repickserver.domain.member.domain.SubscriberInfo;
import repick.repickserver.domain.member.dto.SubscribeHistoryResponse;
import repick.repickserver.domain.member.dto.SubscriberInfoResponse;

import java.time.LocalDateTime;

@Service @RequiredArgsConstructor
public class SubscriberInfoMapper {

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

    public SubscribeHistoryResponse toSubscribeHistoryResponse(SubscriberInfo subscriberInfo) {
        return SubscribeHistoryResponse.builder()
                .orderNumber(subscriberInfo.getOrderNumber())
                .createdDate(subscriberInfo.getCreatedDate())
                .expireDate(subscriberInfo.getExpireDate())
                .subscribeState(subscriberInfo.getSubscribeState())
                .subscribeType(subscriberInfo.getSubscribeType())
                .build();
    }

    public SubscriberInfoResponse toSubscriberInfoResponse(SubscriberInfo subscriberInfo) {
        return SubscriberInfoResponse.builder()
                .id(subscriberInfo.getId())
                .email(subscriberInfo.getMember().getEmail())
                .name(subscriberInfo.getMember().getName())
                .nickname(subscriberInfo.getMember().getNickname())
                .phoneNumber(subscriberInfo.getMember().getPhoneNumber())
                .orderNumber(subscriberInfo.getOrderNumber())
                .createdDate(subscriberInfo.getCreatedDate())
                .expireDate(subscriberInfo.getExpireDate())
                .subscribeState(subscriberInfo.getSubscribeState())
                .subscribeType(subscriberInfo.getSubscribeType())
                .build();
    }

    public SubscriberInfoResponse toSubscriberInfoResponse(SubscriberInfo subscriberInfo, Member member) {
        return SubscriberInfoResponse.builder()
                .id(subscriberInfo.getId())
                .orderNumber(subscriberInfo.getOrderNumber())
                .createdDate(subscriberInfo.getCreatedDate())
                .expireDate(subscriberInfo.getExpireDate())
                .subscribeState(subscriberInfo.getSubscribeState())
                .subscribeType(subscriberInfo.getSubscribeType())
                .email(member.getEmail())
                .name(member.getName())
                .nickname(member.getNickname())
                .phoneNumber(member.getPhoneNumber())
                .build();
    }
}
