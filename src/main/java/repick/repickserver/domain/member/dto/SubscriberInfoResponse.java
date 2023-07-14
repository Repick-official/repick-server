package repick.repickserver.domain.member.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import repick.repickserver.domain.member.domain.SubscribeState;

import java.time.LocalDateTime;

@Getter
@Builder @AllArgsConstructor @NoArgsConstructor
public class SubscriberInfoResponse {
    @Schema(description = "DB 식별용 아이디", example = "1")
    private Long id;
    @Schema(description = "주문번호", example = "S230523D7BQ1")
    private String orderNumber;
    @Schema(description = "만료일", example = "2021-08-31T00:00:00")
    private LocalDateTime expireDate;
    @Schema(description = "생성일", example = "2021-08-31T00:00:00")
    private LocalDateTime createdDate;
    @Schema(description = "구독상태", example = "REQUESTED")
    private SubscribeState subscribeState;

}
