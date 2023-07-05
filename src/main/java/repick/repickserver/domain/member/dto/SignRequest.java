package repick.repickserver.domain.member.dto;

import lombok.Getter;
import lombok.Setter;
import repick.repickserver.domain.member.domain.Address;

@Getter @Setter
public class SignRequest {

    private Long id;

    private String password;

    private String nickname;

    private String name;

    private String email;

    private Address address;

//    private String mainAddress;
//
//    private String detailAddress;
//
//    private String zipCode;

    private String phoneNumber;

}
