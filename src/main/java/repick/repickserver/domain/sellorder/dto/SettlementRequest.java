package repick.repickserver.domain.sellorder.dto;

import lombok.Getter;

import java.util.List;

@Getter
public class SettlementRequest {
    private List<Long> productIds;
}
