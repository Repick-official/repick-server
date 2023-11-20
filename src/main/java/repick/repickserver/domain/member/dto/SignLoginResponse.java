package repick.repickserver.domain.member.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import repick.repickserver.domain.model.Address;
import repick.repickserver.domain.member.domain.Role;
import repick.repickserver.domain.model.Bank;


@Getter
@Builder @AllArgsConstructor @NoArgsConstructor
public class SignLoginResponse {
    @Schema(description = "닉네임", example = "tester")
    private String nickname;
    @Schema(description = "실명", example = "김리픽")
    private String name;
    @Schema(description = "이메일", example = "test@example.com")
    private String email;
    @Schema(description = "권한", example = "USER")
    private Role role;
    @Schema(description = "전화번호", example = "01012345678")
    private String phoneNumber;
    private Address address;
    private Bank bank;
    private Boolean allowMarketingMessages;
    @Schema(description = "엑세스 토큰")
    private String accessToken;
    @Schema(description = "리프레쉬 토큰")
    private String refreshToken;

}
