package repick.repickserver.domain.order.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.lang.Nullable;
import repick.repickserver.domain.model.Address;
import repick.repickserver.domain.member.domain.Member;
import repick.repickserver.domain.model.BaseTimeEntity;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Getter
@Entity
@Builder @AllArgsConstructor @NoArgsConstructor
public class SellOrder extends BaseTimeEntity {

    @Id @GeneratedValue
    private Long id;

    /*
    * 주문 번호
    * 업데이트 시 주문 번호 추적하기 위함
     */
    private Long orderId;

    @NotNull
    private String name;

    @NotNull
    private String phoneNumber;

    // 계좌 은행, 번호
    @NotNull
    private String bankName;

    @NotNull
    private String accountNumber;

    // 리픽백 수량
    @NotNull
    private Integer bagQuantity;

    // 의류 수량
    @NotNull
    private Integer productQuantity;

    @NotNull
    @Embedded
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
