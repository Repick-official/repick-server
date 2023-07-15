package repick.repickserver.domain.member.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter @Builder @AllArgsConstructor @NoArgsConstructor
public class SubscriberInfoRequest {
    @Schema(description = "주문번호", example = "S230715F8W4M")
    private String orderNumber;

}
