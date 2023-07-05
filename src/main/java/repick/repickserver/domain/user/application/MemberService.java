package repick.repickserver.domain.user.application;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import repick.repickserver.domain.user.dao.MemberRepository;
import repick.repickserver.domain.user.domain.Address;
import repick.repickserver.domain.user.domain.Member;
import repick.repickserver.domain.user.domain.Role;
import repick.repickserver.domain.user.dto.SignRequest;
import repick.repickserver.domain.user.dto.SignResponse;
import repick.repickserver.global.jwt.JwtProvider;
import repick.repickserver.global.jwt.UserDetailsImpl;

import javax.transaction.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;

    public SignResponse login(SignRequest request) throws Exception {
        Member member = memberRepository.findByEmail(request.getEmail()).orElseThrow(() ->
                new BadCredentialsException("잘못된 계정정보입니다."));

        if (!passwordEncoder.matches(request.getPassword(), member.getPassword())) {
            throw new BadCredentialsException("잘못된 계정정보입니다.");
        }

        return SignResponse.builder()
                .id(member.getId())
                .name(member.getName())
                .email(member.getEmail())
                .nickname(member.getNickname())
                .role(member.getRole())
                .accessToken(jwtProvider.createAccessToken(new UserDetailsImpl(member)))
                .refreshToken(jwtProvider.createRefreshToken(new UserDetailsImpl(member)))
                .build();

    }

    public boolean register(SignRequest request) throws Exception {
        try {
//            Address address = Address.builder()
//                    .mainAddress(request.getMainAddress())
//                    .detailAddress(request.getDetailAddress())
//                    .zipCode(request.getZipCode())
//                    .build();
            Member member = Member.builder()
                    .password(passwordEncoder.encode(request.getPassword()))
                    .name(request.getName())
                    .nickname(request.getNickname())
                    .email(request.getEmail())
                    .phoneNumber(request.getPhoneNumber())
                    .address(request.getAddress())
                    .role(Role.USER)
                    .build();

            memberRepository.save(member);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            throw new Exception("잘못된 요청입니다.");
        }
        return true;
    }

    public SignResponse getMember(String email) throws Exception {
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new Exception("계정을 찾을 수 없습니다."));
        return new SignResponse(member);
    }

    // update
    public boolean update(SignRequest request) throws Exception {
        // TODO : find user using token
    


//        member = Member.builder()
//                        .name(request.getName())
//                        .nickname(request.getNickname())
//                        .password(passwordEncoder.encode(request.getPassword()))
//                        .role(Role.USER)
//                        .build();
//
//        memberRepository.save(member);

        return true;
    }
}