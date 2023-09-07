package repick.repickserver.domain.member.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@Builder @NoArgsConstructor @AllArgsConstructor
public class SocialUserInfoDto {
    @Schema(description = "DB 식별용 아이디", example = "1")
    private Long id;
    @Schema(description = "닉네임", example = "tester")
    private String nickname;
    @Schema(description = "이메일", example = "test@example.com")
    private String email;
    @Schema(description = "엑세스 토큰")
    private String accessToken;
    @Schema(description = "리프레쉬 토큰")
    private String refreshToken;

    public static SocialUserInfoDto of(Long id, String nickname, String email) {
        return SocialUserInfoDto.builder()
                .id(id)
                .nickname(nickname)
                .email(email)
                .build();
    }

    public static SocialUserInfoDto of(Long id, String nickname) {
        return SocialUserInfoDto.builder()
                .id(id)
                .nickname(nickname)
                .build();
    }

}