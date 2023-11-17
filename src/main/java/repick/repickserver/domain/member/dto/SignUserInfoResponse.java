package repick.repickserver.domain.member.dto;

import com.querydsl.core.annotations.QueryProjection;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import repick.repickserver.domain.member.domain.Member;
import repick.repickserver.domain.model.Address;
import repick.repickserver.domain.member.domain.Role;
import repick.repickserver.domain.model.Bank;


@Getter
@AllArgsConstructor @NoArgsConstructor
public class SignUserInfoResponse { // SignUpdateResponse 도 이걸로 대체
    private Long id;
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

    @Builder @QueryProjection
    public SignUserInfoResponse(Member member) {
        this.id = member.getId();
        this.nickname = member.getNickname();
        this.name = member.getName();
        this.email = member.getEmail();
        this.phoneNumber = member.getPhoneNumber();
        this.address = member.getAddress();
        this.bank = member.getBank();
        this.allowMarketingMessages = member.getAllowMarketingMessages();
    }

}
