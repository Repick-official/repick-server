package repick.repickserver.domain.order.dto;

import lombok.Builder;
import lombok.Getter;
import repick.repickserver.domain.member.domain.Address;
import repick.repickserver.domain.order.domain.Order;

import javax.persistence.Embedded;
import java.time.LocalDateTime;

@Getter
@Builder
public class SellOrderResponse {

    private Order order;

    // info of sell order
    private Integer bagQuantity;
    private Integer productQuantity;
    private LocalDateTime returnDate;

}
