package repick.repickserver.domain.order.dto;

import lombok.Builder;
import lombok.Getter;
import repick.repickserver.domain.member.domain.Address;
import repick.repickserver.domain.order.domain.SellOrder;
import repick.repickserver.domain.order.domain.SellState;

import javax.persistence.Embedded;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
public class SellOrderResponse {

    private Long id;
    private SellOrder parentSellOrder;
    private List<SellOrder> childSellOrders;
    private String name;
    private String phoneNumber;
    private String bankName;
    private String accountNumber;
    private Integer bagQuantity;
    private Integer productQuantity;
    @Embedded
    private Address address;
    private String requestDetail;
    private LocalDateTime returnDate;
    private SellState sellState;

}
