package repick.repickserver.domain.order.domain;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.lang.Nullable;
import repick.repickserver.domain.model.Address;
import repick.repickserver.domain.member.domain.Member;
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

    /*
    * 판매 주문의 부모 주문 : REQUESTED 상태의 주문을 가리킨다.
    * 판매 주문의 부모 주문이 존재한다는 것은 판매 주문이 취소되었거나, 판매 진행중이라는 것을 의미한다.
     */
    @ManyToOne @JoinColumn(name = "parent_sell_order_id")
    @JsonManagedReference
    private SellOrder parentSellOrder;

    /*
    * 판매 주문의 자식 주문 : CANCELED, DELIVERED, PUBLISHED 상태의 주문들을 가리킨다.
     */
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "parentSellOrder", cascade = CascadeType.ALL)
    @JsonBackReference
    private List<SellOrder> childSellOrders;

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

    @Enumerated(EnumType.STRING)
    private SellState sellState;

    @ManyToOne
    private Member member;

}
