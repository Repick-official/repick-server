package repick.repickserver.domain.user.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import repick.repickserver.domain.user.domain.Member;
import repick.repickserver.domain.user.domain.Role;


@Getter
@Builder @AllArgsConstructor @NoArgsConstructor
public class SignResponse {

    private Long id;

    private String nickname;

    private String name;

    private String email;

    private Role role;

    private String accessToken;
    private String refreshToken;

    public SignResponse(Member member) {
        this.id = member.getId();
        this.nickname = member.getNickname();
        this.name = member.getName();
        this.email = member.getEmail();
        this.role = member.getRole();
    }
}
