package repick.repickserver.domain.member.dto;

import lombok.*;
import repick.repickserver.domain.member.domain.Address;
import repick.repickserver.domain.member.domain.Member;
import repick.repickserver.domain.member.domain.Role;


@Getter
@Builder @AllArgsConstructor @NoArgsConstructor
public class SignResponse {
    private String nickname;
    private String name;
    private String email;
    private Role role;
    private String phoneNumber;
    private Address address;

}
