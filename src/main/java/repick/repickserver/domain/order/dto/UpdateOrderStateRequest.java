package repick.repickserver.domain.order.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

import javax.validation.constraints.NotBlank;

@Getter
public class UpdateOrderStateRequest {

    @NotBlank
    @Schema(description = "주문번호", example = "R2302188GHE1")
    private String orderNumber;

    @NotBlank
    @Schema(description = "주문상태 (UNPAID, PREPARING, DELIVERING, DELIVERED, CANCELED)", example = "DELIVERING")
    private String orderState;
}
