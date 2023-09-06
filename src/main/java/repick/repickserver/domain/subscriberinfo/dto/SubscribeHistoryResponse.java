package repick.repickserver.domain.subscriberinfo.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import repick.repickserver.domain.subscriberinfo.domain.SubscribeState;
import repick.repickserver.domain.subscriberinfo.domain.SubscribeType;
import repick.repickserver.domain.subscriberinfo.domain.SubscriberInfo;

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

    public static SubscribeHistoryResponse of(SubscriberInfo subscriberInfo) {
        return SubscribeHistoryResponse.builder()
                .orderNumber(subscriberInfo.getOrderNumber())
                .createdDate(subscriberInfo.getCreatedDate())
                .expireDate(subscriberInfo.getExpireDate())
                .subscribeState(subscriberInfo.getSubscribeState())
                .subscribeType(subscriberInfo.getSubscribeType())
                .build();
    }

}
