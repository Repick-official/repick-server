package repick.repickserver.domain.member.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.time.LocalDateTime;


@Entity
@EntityListeners(AuditingEntityListener.class)
@Getter @Builder @AllArgsConstructor @NoArgsConstructor
public class SubscriberInfo {

    @Id @GeneratedValue
    private Long id;

    @OneToOne
    @JoinColumn(name = "member_id")
    private Member member;

    private LocalDateTime expireDate;

    @CreatedDate
    private LocalDateTime createdAt;


}
