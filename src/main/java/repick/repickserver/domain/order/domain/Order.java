package repick.repickserver.domain.order.domain;

import lombok.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.lang.Nullable;
import repick.repickserver.domain.model.Address;
import repick.repickserver.domain.member.domain.Member;
import repick.repickserver.domain.model.BaseTimeEntity;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Getter
@Builder
@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "orders")
@AllArgsConstructor
@NoArgsConstructor
public class Order extends BaseTimeEntity {

    @Id @GeneratedValue
    private Long id;

    // 주문자 이름 (회원 이름과 다를 수 있음에 주의)
    @NotNull
    private String name;

    // 주문수량 (ERD에서 orderNumber -> Quantity 이름 변경)
    @Nullable
    private Integer quantity;

    // 주소, 요청사항, 전화번호를 따로 받는 이유 : 주문마다 정보가 다를 수 있음 (ex. 회사, 집, 학교)
    @NotNull
    @Embedded
    private Address address;

    // 주문/배달시 요청사항
    @Nullable
    private String requestDetail;

    @NotNull
    private String phoneNumber;

    //주문상태
    private OrderState orderState;

    // 주문자 (한명의 멤버는 여러개의 주문을 가질 수 있음)
    @ManyToOne
    private Member member;

    // BaseTimeEntity 사용
    //    @CreatedDate
    //    private LocalDateTime createdAt;
    //
    //    @LastModifiedDate
    //    private LocalDateTime updatedAt;


}
