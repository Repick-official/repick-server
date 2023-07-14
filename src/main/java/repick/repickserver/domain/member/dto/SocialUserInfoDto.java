package repick.repickserver.domain.member.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@Setter // TODO : HTTPS 설정 후 삭제
@NoArgsConstructor
@AllArgsConstructor
public class SocialUserInfoDto {
    @Schema(description = "DB 식별용 아이디", example = "1")
    private Long id;
    @Schema(description = "닉네임", example = "tester")
    private String nickname;
    @Schema(description = "이메일", example = "test@example.com")
    private String email;
    @Schema(description = "엑세스 토큰")
    private String accessToken;

}