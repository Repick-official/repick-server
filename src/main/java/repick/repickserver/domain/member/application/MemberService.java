package repick.repickserver.domain.member.application;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import repick.repickserver.domain.cart.dao.CartRepository;
import repick.repickserver.domain.cart.domain.Cart;
import repick.repickserver.domain.member.dao.MemberRepository;
import repick.repickserver.domain.member.domain.Member;
import repick.repickserver.domain.member.domain.Role;
import repick.repickserver.domain.member.dto.SignLoginRequest;
import repick.repickserver.domain.member.dto.SignLoginResponse;
import repick.repickserver.domain.member.dto.SignUpdateRequest;
import repick.repickserver.domain.member.dto.SignUserInfoResponse;
import repick.repickserver.global.error.exception.CustomException;
import repick.repickserver.global.jwt.JwtProvider;
import repick.repickserver.global.jwt.UserDetailsImpl;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import javax.transaction.Transactional;
import java.util.UUID;

import static repick.repickserver.global.error.exception.ErrorCode.*;

@Service
@Transactional
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;
    private final CartRepository cartRepository;

    /**
     * 로그인
     * @param request (email, password)
     * @param response (accessToken, refreshToken 반환하기 위함)
     * @return SignResponse (name, email, nickname, role, phoneNumber, address, accessToken, refreshToken)
     * @apiNote
     * 1. 이메일로 멤버를 찾는다.
     * 2. 멤버가 없으면 에러를 던진다.
     * 3. 비밀번호가 일치하지 않으면 에러를 던진다.
     * 4. JWT 토큰을 생성하여 쿠키에 담아서 반환한다.
     * @author seochanhyeok
     */
    public SignLoginResponse login(SignLoginRequest request, HttpServletResponse response) {
        Member member = memberRepository.findByEmail(request.getEmail()).orElseThrow(() ->
                new CustomException("이메일에 해당하는 멤버를 찾을 수 없습니다.", MEMBER_NOT_FOUND));

        if (!passwordEncoder.matches(request.getPassword(), member.getPassword())) {
            throw new CustomException("비밀번호가 올바르지 않습니다.", MEMBER_NOT_FOUND);
        }

        String accessToken = jwtProvider.createAccessToken(new UserDetailsImpl(member));
        String refreshToken = jwtProvider.createRefreshToken(new UserDetailsImpl(member));

        response.addCookie(new Cookie("accessToken", accessToken));
        response.addCookie(new Cookie("refreshToken", refreshToken));

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
    public SignUserInfoResponse register(SignUpdateRequest request) {
        try {
            Member member = Member.builder()
                    .userId(UUID.randomUUID().toString())
                    .password(passwordEncoder.encode(request.getPassword()))
                    .email(request.getEmail())
                    .name(request.getName())
                    .nickname(request.getNickname())
                    .phoneNumber(request.getPhoneNumber())
                    .address(request.getAddress())
                    .bank(request.getBank())
                    .role(Role.USER)
                    .build();

            memberRepository.save(member);

            // 장바구니 생성
            cartRepository.save(Cart.builder()
                    .member(member)
                    .build());

            return SignUserInfoResponse.builder()
                    .name(member.getName())
                    .email(member.getEmail())
                    .nickname(member.getNickname())
                    .role(member.getRole())
                    .phoneNumber(member.getPhoneNumber())
                    .address(member.getAddress())
                    .bank(member.getBank())
                    .build();

        } catch (Exception e) {
            System.out.println(e.getMessage());
            throw new CustomException("회원 등록에 실패했습니다", MEMBER_REGISTER_FAIL);
        }
    }

    /**
     * 멤버 조회
     * @param email (email)
     * @return SignResponse (name, email, nickname, role, phoneNumber, address, bank)
     * @exception CustomException (MEMBER_NOT_FOUND)
     * @apiNote
     * 1. 이메일로 멤버를 찾는다.
     * 2. 멤버가 없으면 에러를 던진다.
     * 3. 멤버를 반환한다.
     * @author seochanhyeok
     */
    public SignUserInfoResponse getMember(String email) {
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException("이메일에 해당하는 멤버를 찾을 수 없습니다.", MEMBER_NOT_FOUND));
        return SignUserInfoResponse.builder()
                .name(member.getName())
                .email(member.getEmail())
                .nickname(member.getNickname())
                .role(member.getRole())
                .phoneNumber(member.getPhoneNumber())
                .address(member.getAddress())
                .bank(member.getBank())
                .build();
    }

    /**
     * 멤버 조회
     * @param token (accessToken)
     * @return SignResponse (name, email, nickname, role, phoneNumber, address, bank)
     * @exception CustomException (TOKEN_MEMBER_NO_MATCH) 토큰에 해당하는 멤버의 userId를 찾을 수 없을 때
     * @apiNote
     * 1. 토큰으로 멤버를 찾는다.
     * 2. 멤버가 없으면 에러를 던진다.
     * 3. 멤버를 반환한다.
     * 4. 토큰이 만료되었으면 에러를 던진다.
     * @author seochanhyeok
     */
    public SignUserInfoResponse userInfo(String token) {
        Member member = jwtProvider.getMemberByRawToken(token);
        return SignUserInfoResponse.builder()
                .name(member.getName())
                .email(member.getEmail())
                .nickname(member.getNickname())
                .role(member.getRole())
                .phoneNumber(member.getPhoneNumber())
                .address(member.getAddress())
                .bank(member.getBank())
                .build();
    }

    /**
     * 멤버 수정
     * @param request (email, password, name, nickname, phoneNumber, address, bank)
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
    public SignUserInfoResponse update(SignUpdateRequest request, String token) {
        Member member = jwtProvider.getMemberByRawToken(token);

        // null인 항목들은 기존 정보로 가져옴
        if (request.getEmail() == null) {
            request.setEmail(member.getEmail());
        }
        if (request.getPassword() == null) {
            request.setPassword(member.getPassword());
        }
        if (request.getName() == null) {
            request.setName(member.getName());
        }
        if (request.getNickname() == null) {
            request.setNickname(member.getNickname());
        }
        if (request.getPhoneNumber() == null) {
            request.setPhoneNumber(member.getPhoneNumber());
        }
        if (request.getAddress() == null) {
            request.setAddress(member.getAddress());
        }
        if (request.getBank() == null) {
            request.setBank(member.getBank());
        }

        member.update(request.getEmail(),
                passwordEncoder.encode(request.getPassword()),
                request.getNickname(),
                request.getName(),
                request.getPhoneNumber(),
                request.getAddress(),
                request.getBank());

        memberRepository.save(member);

        return SignUserInfoResponse.builder()
                .name(member.getName())
                .email(member.getEmail())
                .nickname(member.getNickname())
                .role(member.getRole())
                .phoneNumber(member.getPhoneNumber())
                .address(member.getAddress())
                .bank(member.getBank())
                .build();
    }


    public String refresh(String token) {

        token = "Bearer " + token;
        System.out.println("token = " + token);
        // 리프레쉬 토큰 검사
        if (!jwtProvider.validateToken(token)) {
            throw new CustomException("토큰이 만료되었습니다.", TOKEN_EXPIRED);
        }
        Member member = jwtProvider.getMemberByRawToken(token);
        return jwtProvider.createAccessToken(new UserDetailsImpl(member));

    }
}