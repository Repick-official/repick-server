package repick.repickserver.domain.member.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import repick.repickserver.domain.member.domain.SubscribeState;
import repick.repickserver.domain.member.domain.SubscribeType;

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
    @Schema(description = "만료일", example = "2021-08-31T00:00:00")
    private LocalDateTime expireDate;
    @Schema(description = "생성일", example = "2021-08-31T00:00:00")
    private LocalDateTime createdDate;
    @Schema(description = "구독상태", example = "REQUESTED")
    private SubscribeState subscribeState;
    @Schema(description = "플랜 이름", example = "BASIC")
    private SubscribeType subscribeType;

}
