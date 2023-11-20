package repick.repickserver.domain.sellorder.dto;

import lombok.Getter;
import repick.repickserver.domain.model.Bank;

import javax.persistence.Embedded;
import java.util.List;

@Getter
public class SettlementRequest {
    private List<Long> productIds;
    @Embedded
    private Bank bank;

}
