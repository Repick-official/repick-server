package repick.repickserver.domain.sellorder.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Getter
public class BagReadyRequest {

    @Schema(description = "주문번호", example = "R2302188GHE1")
    private String orderNumber;

    @Schema(description = "실제 배출 수량", example = "1")
    private int bagQuantity;
}
