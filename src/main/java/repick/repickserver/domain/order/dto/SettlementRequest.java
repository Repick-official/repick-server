package repick.repickserver.domain.order.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

import java.util.List;

@Getter
public class SettlementRequest {
    @Schema(description = "상품 아이디", example = "4")
    private List<Long> productIds;
}
