package repick.repickserver.domain.order.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.lang.Nullable;
import repick.repickserver.domain.member.domain.Member;
import repick.repickserver.domain.model.Address;
import repick.repickserver.domain.model.Bank;
import repick.repickserver.domain.model.BaseTimeEntity;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter @Builder @AllArgsConstructor @NoArgsConstructor
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
    @NotNull @Embedded
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

    /*
    * 원하는 수거 날짜 시간
    * null : "시간은 딱히 상관없어요" 체크한 경우
     */
    @Nullable
    private LocalDateTime returnDate;

    @ManyToOne
    private Member member;

}
