package repick.repickserver.domain.member.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import repick.repickserver.domain.member.domain.SubscribeState;
import repick.repickserver.domain.member.domain.SubscribeType;

import java.time.LocalDateTime;

@Getter @Builder
public class SubscribeHistoryResponse {

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

}
