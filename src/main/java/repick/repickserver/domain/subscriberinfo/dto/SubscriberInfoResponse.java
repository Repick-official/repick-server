package repick.repickserver.domain.subscriberinfo.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import repick.repickserver.domain.member.domain.Member;
import repick.repickserver.domain.subscriberinfo.domain.SubscribeState;
import repick.repickserver.domain.subscriberinfo.domain.SubscribeType;
import repick.repickserver.domain.subscriberinfo.domain.SubscriberInfo;

import java.time.LocalDateTime;

@Getter
@Builder @AllArgsConstructor @NoArgsConstructor
public class SubscriberInfoResponse {
    @Schema(description = "DB 식별용 아이디", example = "1")
    private Long id;
    @Schema(description = "구독 신청자 이메일", example = "test@example.com")
    private String email;
    @Schema(description = "구독 신청자 이름", example = "김리픽")
    private String name;
    @Schema(description = "구독 신청자 닉네임", example = "리픽")
    private String nickname;
    @Schema(description = "구독 신청자 전화번호", example = "01012345678")
    private String phoneNumber;
    @Schema(description = "주문번호", example = "S230523D7BQ1")
    private String orderNumber;
    @Schema(description = "만료일")
    @JsonFormat(pattern = "yyyy. MM. dd. HH:mm")
    private LocalDateTime expireDate;
    @Schema(description = "생성일")
    @JsonFormat(pattern = "yyyy. MM. dd. HH:mm")
    private LocalDateTime createdDate;
    @Schema(description = "구독상태", example = "REQUESTED")
    private SubscribeState subscribeState;
    @Schema(description = "플랜 이름", example = "BASIC")
    private SubscribeType subscribeType;

    public static SubscriberInfoResponse from(SubscriberInfo subscriberInfo) {
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

    public static SubscriberInfoResponse of(SubscriberInfo subscriberInfo, Member member) {
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
