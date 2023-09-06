package repick.repickserver.domain.order.dto;

import lombok.Getter;
import org.springframework.lang.Nullable;
import repick.repickserver.domain.member.domain.Member;
import repick.repickserver.domain.model.Address;
import repick.repickserver.domain.order.domain.Order;

import javax.persistence.Embedded;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

@Getter
public class OrderRequest {

    private List<Long> productIds;

    @NotBlank
    private String personName;

    @NotBlank
    private String phoneNumber;

    @NotNull
    @Embedded
    private Address address;

    @Nullable
    private String requestDetail;

    public static Order toOrder(Member member, OrderRequest orderRequest, String orderNumber) {
        return Order.builder()
                .member(member)
                .personName(orderRequest.getPersonName())
                .phoneNumber(orderRequest.getPhoneNumber())
                .address(orderRequest.getAddress())
                .requestDetail(orderRequest.getRequestDetail())
                .orderNumber(orderNumber)
                .build();
    }

}
