package repick.repickserver.domain.member.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import repick.repickserver.domain.model.Address;

@Getter @Setter
public class SignRequest {

    @Schema(description = "DB 식별용 아이디")
    private Long id;

    @Schema(description = "비밀번호", example = "123")
    private String password;

    @Schema(description = "닉네임", example = "tester")
    private String nickname;

    @Schema(description = "실명", example = "김리픽")
    private String name;

    @Schema(description = "이메일", example = "test@example.com")
    private String email;

    private Address address;

    @Schema(description = "전화번호", example = "01012345678")
    private String phoneNumber;

    @Schema(description = "기본 은행계좌", example = "카카오뱅크")
    private String bankName;

    @Schema(description = "기본 계좌번호", example = "3333980010619")
    private String accountNumber;

}
