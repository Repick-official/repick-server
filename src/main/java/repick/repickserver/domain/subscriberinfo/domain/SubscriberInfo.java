package repick.repickserver.domain.subscriberinfo.domain;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.lang.Nullable;
import repick.repickserver.domain.member.domain.Member;
import repick.repickserver.domain.model.BaseTimeEntity;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;


@Entity
@Getter @Builder @AllArgsConstructor @NoArgsConstructor
public class SubscriberInfo extends BaseTimeEntity {

    @Id @GeneratedValue
    private Long id;

    private String orderNumber;

    @ManyToOne @JoinColumn(name = "parent_subscriber_info_id")
    @JsonManagedReference
    private SubscriberInfo parentSubscriberInfo;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "parentSubscriberInfo", cascade = CascadeType.ALL)
    @JsonBackReference
    private List<SubscriberInfo> childSubscriberInfos;

    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "member_id")
    private Member member;

    /*
    * 구독 종류 : BASIC, PRO, PREMIUM
     */
    @Enumerated(EnumType.STRING)
    private SubscribeType subscribeType;

    /*
    * 구독 상태
    * 요청 : REQUEST
    * 승인 : APPROVE
    * 거절 : REJECT
     */
    @Enumerated(EnumType.STRING)
    private SubscribeState subscribeState;

    /*
    * 구독 만료일
    * 요청 : 7일 뒤 만료
    * 승인 : 30일 뒤 만료
    * 거절 : null
     */
    @Nullable
    private LocalDateTime expireDate;


}
