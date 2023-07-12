package repick.repickserver.domain.order.dto;

import lombok.Getter;
import repick.repickserver.domain.member.domain.Address;
import repick.repickserver.domain.order.domain.SellState;

import javax.persistence.Embedded;
import java.time.LocalDateTime;

@Getter
public class SellOrderRequest {

    private Long id;
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
