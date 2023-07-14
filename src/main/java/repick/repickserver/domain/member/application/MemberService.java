package repick.repickserver.domain.member.application;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import repick.repickserver.domain.member.dao.MemberRepository;
import repick.repickserver.domain.member.domain.Member;
import repick.repickserver.domain.member.domain.Role;
import repick.repickserver.domain.member.dto.SignRequest;
import repick.repickserver.domain.member.dto.SignResponse;
import repick.repickserver.global.error.exception.CustomException;
import repick.repickserver.global.jwt.JwtProvider;
import repick.repickserver.global.jwt.UserDetailsImpl;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import javax.transaction.Transactional;
import java.util.UUID;

import static repick.repickserver.global.error.exception.ErrorCode.MEMBER_REGISTER_FAIL;
import static repick.repickserver.global.error.exception.ErrorCode.MEMBER_NOT_FOUND;

@Service
@Transactional
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;

    /**
     * 로그인
     * @param request (email, password)
     * @param response (accessToken, refreshToken 반환하기 위함)
     * @return SignResponse (name, email, nickname, role)
     * @apiNote
     * 1. 이메일로 멤버를 찾는다.
     * 2. 멤버가 없으면 에러를 던진다.
     * 3. 비밀번호가 일치하지 않으면 에러를 던진다.
     * 4. JWT 토큰을 생성하여 쿠키에 담아서 반환한다.
     * @author seochanhyeok
     */
    public SignResponse login(SignRequest request, HttpServletResponse response) {
        Member member = memberRepository.findByEmail(request.getEmail()).orElseThrow(() ->
                new CustomException("이메일에 해당하는 멤버를 찾을 수 없습니다.", MEMBER_NOT_FOUND));

        if (!passwordEncoder.matches(request.getPassword(), member.getPassword())) {
            throw new CustomException("비밀번호가 올바르지 않습니다.", MEMBER_NOT_FOUND);
        }

        response.addCookie(new Cookie("accessToken", jwtProvider.createAccessToken(new UserDetailsImpl(member))));
        response.addCookie(new Cookie("refreshToken", jwtProvider.createAccessToken(new UserDetailsImpl(member))));

        return SignResponse.builder()
                .name(member.getName())
                .email(member.getEmail())
                .nickname(member.getNickname())
                .role(member.getRole())
                .build();

    }

    /**
     * 회원가입
     * @param request (email, password, name, nickname, phoneNumber, address)
     * @return true
     * @exception CustomException (MEMBER_REGISTER_FAIL)
     * @apiNote
     * 1. 멤버를 생성 후 저장한다.
     * 2. 저장에 실패하면 에러를 던진다.
     * 3. 회원가입 시 기본 권한은 USER이다.
     * 4. 비밀번호는 암호화하여 저장한다.
     * @author seochanhyeok
     */
    public boolean register(SignRequest request) {
        try {
            Member member = Member.builder()
                    .userId(UUID.randomUUID().toString())
                    .password(passwordEncoder.encode(request.getPassword()))
                    .email(request.getEmail())
                    .name(request.getName())
                    .nickname(request.getNickname())
                    .phoneNumber(request.getPhoneNumber())
                    .address(request.getAddress())
                    .role(Role.USER)
                    .build();

            memberRepository.save(member);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            throw new CustomException("회원 등록에 실패했습니다", MEMBER_REGISTER_FAIL);
        }
        return true;
    }

    /**
     * 멤버 조회
     * @param email (email)
     * @return SignResponse (name, email, nickname, role, phoneNumber, address)
     * @exception CustomException (MEMBER_NOT_FOUND)
     * @apiNote
     * 1. 이메일로 멤버를 찾는다.
     * 2. 멤버가 없으면 에러를 던진다.
     * 3. 멤버를 반환한다.
     * @author seochanhyeok
     */
    public SignResponse getMember(String email) {
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException("이메일에 해당하는 멤버를 찾을 수 없습니다.", MEMBER_NOT_FOUND));
        return SignResponse.builder()
                .name(member.getName())
                .email(member.getEmail())
                .nickname(member.getNickname())
                .role(member.getRole())
                .phoneNumber(member.getPhoneNumber())
                .address(member.getAddress())
                .build();
    }

    /**
     * 멤버 조회
     * @param token (accessToken)
     * @return SignResponse (name, email, nickname, role, phoneNumber, address)
     * @exception CustomException (TOKEN_MEMBER_NO_MATCH) 토큰에 해당하는 멤버의 userId를 찾을 수 없을 때
     * @apiNote
     * 1. 토큰으로 멤버를 찾는다.
     * 2. 멤버가 없으면 에러를 던진다.
     * 3. 멤버를 반환한다.
     * 4. 토큰이 만료되었으면 에러를 던진다.
     * @author seochanhyeok
     */
    public SignResponse userInfo(String token) {
        Member member = jwtProvider.getMemberByRawToken(token);
        return SignResponse.builder()
                .name(member.getName())
                .email(member.getEmail())
                .nickname(member.getNickname())
                .role(member.getRole())
                .phoneNumber(member.getPhoneNumber())
                .address(member.getAddress())
                .build();
    }

    /**
     * 멤버 수정
     * @param request (email, password, name, nickname, phoneNumber, address)
     * @param token (accessToken)
     * @return true
     * @exception CustomException (TOKEN_MEMBER_NO_MATCH) 토큰에 해당하는 멤버의 userId를 찾을 수 없을 때
     * @apiNote
     * 1. 토큰으로 멤버를 찾는다.
     * 2. 멤버가 없으면 에러를 던진다.
     * 3. 멤버를 수정한다.
     * 4. 수정에 성공하면 true를 반환한다.
     * @author seochanhyeok
     */
    public boolean update(SignRequest request, String token) {
        Member member = jwtProvider.getMemberByRawToken(token);

        member.update(request.getEmail(), passwordEncoder.encode(request.getPassword()), request.getNickname(), request.getName(), request.getPhoneNumber(), request.getAddress(), request.getBankName(), request.getAccountNumber());

        memberRepository.save(member);

        return true;
    }


}