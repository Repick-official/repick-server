package repick.repickserver.domain.subscriberinfo.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter @AllArgsConstructor
public class SubscriberInfoRequest {
    @Schema(description = "주문번호", example = "S230715F8W4M")
    private String orderNumber;

}
