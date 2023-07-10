package repick.repickserver.domain.member.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.lang.Nullable;
import repick.repickserver.domain.model.BaseTimeEntity;

import javax.persistence.*;
import java.time.LocalDateTime;


@Entity
@EntityListeners(AuditingEntityListener.class)
@Getter @Builder @AllArgsConstructor @NoArgsConstructor
public class SubscriberInfo extends BaseTimeEntity {

    @Id @GeneratedValue
    private Long id;

    @OneToOne
    @JoinColumn(name = "member_id")
    private Member member;

    /*
    * 구독 상태
    * 요청 : REQUEST
    * 승인 : APPROVE
    * 거절 : REJECT
     */
    @Enumerated
    private SubscribeState subscribeState;

    /*
    * 구독 만료일
    * 요청 : 7일 뒤 만료
    * 승인 : 30일 뒤 만료
    * 거절 : null
     */
    @Nullable
    private LocalDateTime expireDate;

    // BaseTimeEntity 사용
    //    @CreatedDate
    //    private LocalDateTime createdAt;


}
