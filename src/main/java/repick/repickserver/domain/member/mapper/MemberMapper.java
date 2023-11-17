package repick.repickserver.domain.member.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import repick.repickserver.domain.member.domain.Member;
import repick.repickserver.domain.member.domain.Role;
import repick.repickserver.domain.member.dto.SignLoginResponse;
import repick.repickserver.domain.member.dto.SignUpdateRequest;
import repick.repickserver.domain.member.dto.SignUserInfoResponse;

import java.util.UUID;

@Service @RequiredArgsConstructor
public class MemberMapper {

    private final PasswordEncoder passwordEncoder;

    public Member registerRequestToMember(SignUpdateRequest request) {

        if (request.getAllowMarketingMessages() == null) request.setAllowMarketingMessages(false);

        return Member.builder()
                .userId(UUID.randomUUID().toString())
                .password(passwordEncoder.encode(request.getPassword()))
                .email(request.getEmail())
                .name(request.getName())
                .nickname(request.getNickname())
                .phoneNumber(request.getPhoneNumber())
                .address(request.getAddress())
                .bank(request.getBank())
                .role(Role.USER)
                .allowMarketingMessages(request.getAllowMarketingMessages())
                .build();
    }

    public SignLoginResponse toSignLoginResponse(Member member, String accessToken, String refreshToken) {
        return SignLoginResponse.builder()
                .name(member.getName())
                .email(member.getEmail())
                .nickname(member.getNickname())
                .role(member.getRole())
                .phoneNumber(member.getPhoneNumber())
                .address(member.getAddress())
                .bank(member.getBank())
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    public SignUserInfoResponse toSignUserInfoResponse(Member member) {
        return SignUserInfoResponse.builder()
                .member(member).build();
    }

    public Member mapRequestToMember(Member member, SignUpdateRequest request) {
        Member.MemberBuilder memberBuilder = Member.builder();

        memberBuilder.email(request.getEmail() != null ? request.getEmail() : member.getEmail());
        memberBuilder.name(request.getName() != null ? request.getName() : member.getName());
        memberBuilder.password(request.getPassword() != null ? passwordEncoder.encode(request.getPassword()) : member.getPassword());
        memberBuilder.nickname(request.getNickname() != null ? request.getNickname() : member.getNickname());
        memberBuilder.phoneNumber(request.getPhoneNumber() != null ? request.getPhoneNumber() : member.getPhoneNumber());
        memberBuilder.address(request.getAddress() != null ? request.getAddress() : member.getAddress());
        memberBuilder.bank(request.getBank() != null ? request.getBank() : member.getBank());
        memberBuilder.allowMarketingMessages(request.getAllowMarketingMessages() != null ? request.getAllowMarketingMessages() : member.getAllowMarketingMessages());

        return memberBuilder.build();
    }
}
