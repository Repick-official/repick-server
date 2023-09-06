package repick.repickserver.domain.order.domain;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.lang.Nullable;
import repick.repickserver.domain.member.domain.Member;
import repick.repickserver.domain.model.Address;
import repick.repickserver.domain.model.BaseTimeEntity;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "orders")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Order extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    // 주문자 이름 (회원 이름과 다를 수 있음에 주의)
    @NotBlank
    private String personName;

    @NotBlank
    private String phoneNumber;

    @NotNull
    @Embedded
    private Address address;

    // 배송 시 요청 사항
    @Nullable
    private String requestDetail;

    @NotBlank
    private String orderNumber;

    // TODO: 입금자명 추가

    @Builder
    public Order(Member member, String personName, String phoneNumber, Address address, String requestDetail, String orderNumber) {
        this.member = member;
        this.personName = personName;
        this.phoneNumber = phoneNumber;
        this.address = address;
        this.requestDetail = requestDetail;
        this.orderNumber = orderNumber;
    }
}
