package repick.repickserver.domain.order.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import repick.repickserver.domain.member.domain.Address;
import repick.repickserver.domain.member.domain.Member;

import javax.persistence.*;
import java.time.LocalDateTime;

@Builder
@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "orders")
@AllArgsConstructor
@NoArgsConstructor
public class Order {

    @Id @GeneratedValue
    private Long id;

    // 주문수량 (ERD에서 orderNumber -> Quantity 이름 변경)
    private Integer Quantity;

    // 주소, 요청사항, 전화번호를 따로 받는 이유 : 주문마다 정보가 다를 수 있음 (ex. 회사, 집, 학교)
    @Embedded
    private Address address;

    // 주문/배달시 요청사항
    private String request_detail;

    private String phoneNumber;

    //주문상태
    private OrderState orderState;

    @OneToOne(mappedBy = "order")
    private SellInfo sellInfo;

    // 주문자 (한명의 멤버는 여러개의 주문을 가질 수 있음)
    @ManyToOne
    private Member member;

    @CreatedDate
    private LocalDateTime createdAt;

    // updatedAt : 주문상태 업데이트 추적하기 위해 넣었음
    @LastModifiedDate
    private LocalDateTime updatedAt;


}
