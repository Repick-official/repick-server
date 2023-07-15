package repick.repickserver.domain.waybill.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Getter
public class WaybillNumberRequest {

    @Schema(description = "택배사 코드", example = "04")
    private String code;

    @Schema(description = "운송장 번호", example = "123456789")
    private String waybillNumber;

    @Schema(description = "주문 번호", example = "S2305021U8DA")
    private String orderNumber;

}
