package repick.repickserver.domain.member.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Getter
public class SignLoginRequest {
    @Schema(description = "이메일", example = "test@example.com")
    private String email;
    @Schema(description = "비밀번호", example = "123")
    private String password;
}
