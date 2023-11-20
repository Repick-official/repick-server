package repick.repickserver.domain.member.application;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import repick.repickserver.domain.cart.dao.CartRepository;
import repick.repickserver.domain.cart.domain.Cart;
import repick.repickserver.domain.member.repository.MemberRepository;
import repick.repickserver.domain.member.domain.Member;
import repick.repickserver.domain.member.dto.SignLoginRequest;
import repick.repickserver.domain.member.dto.SignLoginResponse;
import repick.repickserver.domain.member.dto.SignUpdateRequest;
import repick.repickserver.domain.member.dto.SignUserInfoResponse;
import repick.repickserver.domain.member.mapper.MemberMapper;
import repick.repickserver.domain.member.validator.MemberValidator;
import repick.repickserver.global.error.exception.CustomException;
import repick.repickserver.global.jwt.JwtProvider;
import repick.repickserver.global.jwt.UserDetailsImpl;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import javax.transaction.Transactional;

import java.util.List;

import static repick.repickserver.global.error.exception.ErrorCode.MEMBER_NOT_FOUND;
import static repick.repickserver.global.error.exception.ErrorCode.TOKEN_EXPIRED;

@Service
@Transactional
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final JwtProvider jwtProvider;
    private final CartRepository cartRepository;
    private final MemberMapper memberMapper;
    private final MemberValidator memberValidator;

    public SignLoginResponse login(SignLoginRequest request, HttpServletResponse response) {
        Member member = memberRepository.findByEmail(request.getEmail()).orElseThrow(() ->
                new CustomException("이메일에 해당하는 멤버를 찾을 수 없습니다.", MEMBER_NOT_FOUND));

        memberValidator.validatePassword(member, request.getPassword());

        String accessToken = jwtProvider.createAccessToken(new UserDetailsImpl(member));
        String refreshToken = jwtProvider.createRefreshToken(new UserDetailsImpl(member));

        response.addCookie(new Cookie("accessToken", accessToken));
        response.addCookie(new Cookie("refreshToken", refreshToken));

        return memberMapper.toSignLoginResponse(member, accessToken, refreshToken);
    }

    public SignUserInfoResponse register(SignUpdateRequest request) {

        Member member = memberMapper.registerRequestToMember(request);

        // 중복 체크
        memberValidator.validateDuplicateEmail(member.getEmail());
        memberValidator.validateDuplicateNickname(member.getNickname());

        memberRepository.save(member);

        // 장바구니 생성
        cartRepository.save(Cart.builder()
                .member(member)
                .build());

        return memberMapper.toSignUserInfoResponse(member);
    }

    public SignUserInfoResponse getUserInfo(String token) {
        Member member = jwtProvider.getMemberByRawToken(token);
        return memberMapper.toSignUserInfoResponse(member);
    }

    public SignUserInfoResponse update(SignUpdateRequest request, String token) {
        Member member = jwtProvider.getMemberByRawToken(token);

        // request의 null은 기존 member 정보로 대체
        Member updatedMember = memberMapper.mapRequestToMember(member, request);

        memberValidator.validateDuplicateEmail(updatedMember.getEmail(), member.getUserId());
        memberValidator.validateDuplicateNickname(updatedMember.getNickname(), member.getUserId());

        member.update(updatedMember);
        memberRepository.save(member);

        return memberMapper.toSignUserInfoResponse(member);
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

    public List<SignUserInfoResponse> getUserInfoPage(Long cursorId, int pageSize) {
        return memberRepository.findPage(cursorId, pageSize);
    }
}