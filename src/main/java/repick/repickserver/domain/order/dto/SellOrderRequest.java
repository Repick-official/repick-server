package repick.repickserver.domain.order.dto;

import lombok.Getter;
import repick.repickserver.domain.member.domain.Address;

import javax.persistence.Embedded;
import java.time.LocalDateTime;

@Getter
public class SellOrderRequest {

    // info of order
    @Embedded
    private Address address;
    private String request_detail;
    private String phoneNumber;

    // info of sell order
    private Integer bagQuantity;
    private Integer productQuantity;
    private LocalDateTime returnDate;

}
