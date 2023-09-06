package repick.repickserver.domain.sellorder.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import repick.repickserver.domain.sellorder.domain.SellState;

@Getter
public class SellOrderUpdateRequest {

    @Schema(description = "주문번호", example = "R2302188GHE1")
    private String orderNumber;

    @Schema(description = "주문상태", example = "DELIVERED")
    private SellState sellState;
}
