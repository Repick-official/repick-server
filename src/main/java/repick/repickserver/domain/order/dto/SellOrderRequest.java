package repick.repickserver.domain.order.dto;

import lombok.Getter;
import repick.repickserver.domain.member.domain.Address;

import javax.persistence.Embedded;
import java.time.LocalDateTime;

@Getter
public class SellOrderRequest {

    // info of order
    private String name;
    @Embedded
    private Address address;
    private String requestDetail;
    private String phoneNumber;

    // info of sell order
    private Integer bagQuantity;
    private Integer productQuantity;
    private LocalDateTime returnDate;
    private String bankName;
    private String accountNumber;

}
