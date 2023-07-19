package repick.repickserver.domain.delivery.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter @Builder
public class DeliveryResponse {

    @Schema(description = "택배사 코드", example = "04")
    private String code;

    @Schema(description = "운송장 번호", example = "123456789")
    private String waybillNumber;

    @Schema(description = "주문 번호", example = "S2305021U8DA")
    private String orderNumber;

}
