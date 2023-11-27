package repick.repickserver.domain.sellorder.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.lang.Nullable;
import repick.repickserver.domain.member.domain.Member;
import repick.repickserver.domain.model.Address;
import repick.repickserver.domain.model.Bank;
import repick.repickserver.domain.model.BaseTimeEntity;
import repick.repickserver.domain.sellorder.dto.SellOrderRequest;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.List;

@Entity
@Getter @AllArgsConstructor @NoArgsConstructor
public class SellOrder extends BaseTimeEntity {

    @Id @GeneratedValue
    private Long id;

    @OneToMany(mappedBy = "sellOrder", cascade = CascadeType.ALL)
    private List<SellOrderState> sellOrderStates;

    @NotNull
    private String orderNumber;

    @NotNull
    private String name;

    @NotNull
    private String phoneNumber;

    // 계좌 은행, 번호
    @Nullable @Embedded
    private Bank bank;

    // 리픽백 수량
    @NotNull
    private Integer bagQuantity;

    // 의류 수량
    @NotNull
    private Integer productQuantity;

    @NotNull @Embedded
    private Address address;

    // 수거 시 기타 요청 사항
    @Nullable
    private String requestDetail;

    @ManyToOne
    private Member member;

    @Builder
    public SellOrder(String orderNumber, String name, String phoneNumber, Bank bank, Integer bagQuantity, Integer productQuantity, Address address, String requestDetail, Member member) {
        this.orderNumber = orderNumber;
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.bank = bank;
        this.bagQuantity = bagQuantity;
        this.productQuantity = productQuantity;
        this.address = address;
        this.requestDetail = requestDetail;
        this.member = member;
    }

    public static SellOrder of(SellOrderRequest request, String orderNumber, Member member) {
        return SellOrder.builder()
                .name(request.getName())
                .orderNumber(orderNumber)
                .phoneNumber(request.getPhoneNumber())
                .bank(request.getBank())
                .bagQuantity(request.getBagQuantity())
                .productQuantity(request.getProductQuantity())
                .address(request.getAddress())
                .requestDetail(request.getRequestDetail())
                .member(member)
                .build();
    }

    public void updateBagQuantity(int bagQuantity) {
        this.bagQuantity = bagQuantity;
    }

}
