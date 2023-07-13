package repick.repickserver.domain.member.dto;

import lombok.*;

@Getter
@Setter // TODO : HTTPS 설정 후 삭제
@NoArgsConstructor
@AllArgsConstructor
public class SocialUserInfoDto {
    private Long id;
    private String nickname;
    private String email;
    private String accessToken;

}