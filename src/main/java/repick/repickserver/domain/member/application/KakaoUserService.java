package repick.repickserver.domain.member.application;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import repick.repickserver.domain.cart.dao.CartRepository;
import repick.repickserver.domain.cart.domain.Cart;
import repick.repickserver.domain.member.repository.MemberRepository;
import repick.repickserver.domain.member.domain.Member;
import repick.repickserver.domain.member.domain.Role;
import repick.repickserver.domain.member.dto.SocialUserInfoDto;
import repick.repickserver.global.properties.OauthProperties;
import repick.repickserver.global.jwt.JwtProvider;
import repick.repickserver.global.jwt.UserDetailsImpl;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class KakaoUserService {
    private final PasswordEncoder passwordEncoder;
    private final MemberRepository memberRepository;
    private final JwtProvider jwtProvider;
    private final OauthProperties oauthProperties;
    private final CartRepository cartRepository;

    /**
     * 카카오 로그인
     * @param code 카카오 로그인 요청 시 발급받은 인가 코드
     * @param response JWT 토큰을 response Header에 추가하기 위해 사용
     * @return SocialUserInfoDto id, nickname, email(선택), accessToken, refreshToken
     * @throws JsonProcessingException JSON 파싱 오류
     * @author seochanhyeok
     */
    public SocialUserInfoDto kakaoLogin(String code, String redirect_uri, HttpServletResponse response) throws JsonProcessingException {
        // 1. "인가 코드"로 "액세스 토큰" 요청
        String accessToken = getAccessToken(code, redirect_uri);

        // 2. 토큰으로 카카오 API 호출
        SocialUserInfoDto kakaoUserInfo = getKakaoUserInfo(accessToken);

        // 3. 카카오ID로 회원가입 처리
        Member kakaoUser = registerKakaoUserIfNeed(kakaoUserInfo);

        // 4. 강제 로그인 처리
        Authentication authentication = forceLogin(kakaoUser);

        // 5. response Header, Body에 JWT 토큰 추가
        return kakaoUsersAuthorizationInput(kakaoUserInfo, authentication, response);
    }

    /**
     * 카카오 API 호출 시 사용하는 액세스 토큰 발급
     * @param code 카카오 로그인 요청 시 발급받은 인가 코드
     * @return accessToken
     * @throws JsonProcessingException JSON 파싱 오류
     * @author seochanhyeok
     */
    private String getAccessToken(String code, String redirect_uri) throws JsonProcessingException {
        // HTTP Header 생성
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

        // HTTP Body 생성
        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "authorization_code");
        body.add("client_id", oauthProperties.getClientId());
        body.add("redirect_uri", redirect_uri);
        body.add("client_secret", oauthProperties.getClientSecret());
        body.add("code", code);

        // HTTP 요청 보내기
        HttpEntity<MultiValueMap<String, String>> kakaoTokenRequest = new HttpEntity<>(body, headers);
        RestTemplate rt = new RestTemplate();
        ResponseEntity<String> response = rt.exchange(
                "https://kauth.kakao.com/oauth/token",
                HttpMethod.POST,
                kakaoTokenRequest,
                String.class
        );

        // HTTP 응답 (JSON) -> 액세스 토큰 파싱
        String responseBody = response.getBody();
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.readTree(responseBody);
        return jsonNode.get("access_token").asText();
    }

    /**
     * 카카오 API 호출 시 사용하는 액세스 토큰으로 카카오 사용자 정보 가져오기
     * @param accessToken 카카오 API 호출 시 사용하는 액세스 토큰
     * @return SocialUserInfoDto id, nickname, email(선택)
     * @throws JsonProcessingException JSON 파싱 오류
     * @author seochanhyeok
     */
    private SocialUserInfoDto getKakaoUserInfo(String accessToken) throws JsonProcessingException {
        // HTTP Header 생성
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + accessToken);
        headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

        // HTTP 요청 보내기
        HttpEntity<MultiValueMap<String, String>> kakaoUserInfoRequest = new HttpEntity<>(headers);
        RestTemplate rt = new RestTemplate();
        ResponseEntity<String> response = rt.exchange(
                "https://kapi.kakao.com/v2/user/me",
                HttpMethod.POST,
                kakaoUserInfoRequest,
                String.class
        );

        return handleKakaoResponse(response.getBody());
    }

    private SocialUserInfoDto handleKakaoResponse(String responseBody) throws JsonProcessingException {

        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.readTree(responseBody);

        Long id = jsonNode.get("id").asLong();

        String nickname = jsonNode.get("properties")
                .get("nickname").asText();

        if (jsonNode.get("kakao_account").get("email") == null)
            return SocialUserInfoDto.of(id, nickname);

        String email = jsonNode.get("kakao_account").get("email").asText();
        return SocialUserInfoDto.of(id, nickname, email);

    }


    /**
     * 카카오 ID로 회원가입 처리
     * @param kakaoUserInfo 카카오 사용자 정보
     * @return Member
     * @apiNote
     * 1. DB에 중복된 email이 있는지 확인하고 없으면 회원가입 처리
     * 2. userId는 카카오 ID를 넣음
     * @author seochanhyeok
     */
    private Member registerKakaoUserIfNeed (SocialUserInfoDto kakaoUserInfo) {
        // DB 에 중복된 email이 있는지 확인
        String userId = kakaoUserInfo.getId().toString();
        String kakaoEmail = kakaoUserInfo.getEmail();
        String nickname = kakaoUserInfo.getNickname();
        Member kakaoUser = memberRepository.findByUserId(userId)
                .orElse(null);

        if (kakaoUser == null) {
            // 회원가입
            // password: random UUID
            String password = UUID.randomUUID().toString();
            String encodedPassword = passwordEncoder.encode(password);

            kakaoUser = Member.builder()
                    .userId(userId)
                    .email(kakaoEmail)
                    .nickname(nickname)
                    .password(encodedPassword)
                    .role(Role.USER)
                    .build();

            memberRepository.save(kakaoUser);

            cartRepository.save(Cart.builder()
                    .member(kakaoUser)
                    .build());

        }
        return kakaoUser;
    }

    /**
     * 강제 로그인 처리
     * @param kakaoUser 카카오 사용자 정보
     * @return Authentication
     * @author seochanhyeok
     */
    private Authentication forceLogin(Member kakaoUser) {
        UserDetails userDetails = new UserDetailsImpl(kakaoUser);
        Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);
        return authentication;
    }

    /**
     * response Header에 JWT 토큰 추가
     * @param authentication 강제 로그인 처리된 Authentication
     * @param response HttpServletResponse
     * @author seochanhyeok
     */
    private SocialUserInfoDto kakaoUsersAuthorizationInput(SocialUserInfoDto kakaoUser, Authentication authentication, HttpServletResponse response) {
        UserDetailsImpl userDetailsImpl = ((UserDetailsImpl) authentication.getPrincipal());
        String accessToken = jwtProvider.createAccessToken(userDetailsImpl);
        String refreshToken = jwtProvider.createRefreshToken(userDetailsImpl);
        response.addCookie(new Cookie("accessToken", accessToken));
        response.addCookie(new Cookie("refreshToken", refreshToken));

        return SocialUserInfoDto.builder()
                .id(kakaoUser.getId())
                .email(kakaoUser.getEmail())
                .nickname(kakaoUser.getNickname())
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }
}