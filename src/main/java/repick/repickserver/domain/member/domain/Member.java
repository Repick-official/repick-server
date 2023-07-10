package repick.repickserver.domain.member.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import repick.repickserver.domain.model.BaseTimeEntity;

import javax.persistence.*;

@Entity
@Getter
@Builder @AllArgsConstructor @NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class Member extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String userId;

    @Column(unique = true)
    private String email;

    private String password;

    @Column(unique = true)
    private String nickname;

    private String name;

    private Role role;

    @Embedded
    private Address address;

    private String phoneNumber;

    // BaseTimeEntity 사용
    //    @CreatedDate
    //    private LocalDateTime createdAt;
    //
    //    @LastModifiedDate
    //    private LocalDateTime updatedAt;

    public void update(String email, String password, String nickname, String name, String phoneNumber, Address address) {
        this.email = email;
        this.password = password;
        this.nickname = nickname;
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.address = address;
    }


}