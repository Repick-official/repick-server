package repick.repickserver.domain.user.dto;

import lombok.Getter;
import lombok.Setter;
import repick.repickserver.domain.user.domain.Address;

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
