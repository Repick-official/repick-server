package repick.repickserver.domain.order.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

import java.util.List;

@Getter
public class SettlementRequest {
    private List<Long> productIds;
}
