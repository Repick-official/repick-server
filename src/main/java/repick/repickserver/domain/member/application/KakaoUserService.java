package repick.repickserver.domain.member.application;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
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
import repick.repickserver.domain.member.dao.MemberRepository;
import repick.repickserver.domain.member.domain.Member;
import repick.repickserver.domain.member.domain.Role;
import repick.repickserver.domain.member.dto.SocialUserInfoDto;
import repick.repickserver.global.config.OauthProperties;
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

    /**
     * 카카오 로그인
     * @param code 카카오 로그인 요청 시 발급받은 인가 코드
     * @param response JWT 토큰을 response Header에 추가하기 위해 사용
     * @return SocialUserInfoDto id, nickname, email(선택)
     * @throws JsonProcessingException JSON 파싱 오류
     * @author seochanhyeok
     */
    public SocialUserInfoDto kakaoLogin(String code, HttpServletResponse response) throws JsonProcessingException {
        // 1. "인가 코드"로 "액세스 토큰" 요청
        String accessToken = getAccessToken(code);

        // 2. 토큰으로 카카오 API 호출
        SocialUserInfoDto kakaoUserInfo = getKakaoUserInfo(accessToken);

        // 3. 카카오ID로 회원가입 처리
        Member kakaoUser = registerKakaoUserIfNeed(kakaoUserInfo);

        // 4. 강제 로그인 처리
        Authentication authentication = forceLogin(kakaoUser);

        // 5. response Header에 JWT 토큰 추가
        kakaoUsersAuthorizationInput(authentication, response);
        return kakaoUserInfo;
    }

    /**
     * 카카오 API 호출 시 사용하는 액세스 토큰 발급
     * @param code 카카오 로그인 요청 시 발급받은 인가 코드
     * @return accessToken
     * @throws JsonProcessingException JSON 파싱 오류
     * @author seochanhyeok
     */
    private String getAccessToken(String code) throws JsonProcessingException {
        // HTTP Header 생성
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

        System.out.println("oauthProperties = " + oauthProperties.getClientId());
        System.out.println("oauthProperties = " + oauthProperties.getClientSecret());
        // HTTP Body 생성
        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "authorization_code");
        body.add("client_id", oauthProperties.getClientId());
        body.add("redirect_uri", "http://localhost:3000/login/kakaoLogin");
        body.add("client_secret", oauthProperties.getClientSecret());
        body.add("code", code);


        System.out.println("oauthProperties = " + oauthProperties.getClientId());
        System.out.println("oauthProperties = " + oauthProperties.getClientSecret());

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

        // responseBody에 있는 정보를 꺼냄
        String responseBody = response.getBody();
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.readTree(responseBody);

        Long id = jsonNode.get("id").asLong();
        String email = jsonNode.get("kakao_account").get("email").asText();
        String nickname = jsonNode.get("properties")
                .get("nickname").asText();

        return new SocialUserInfoDto(id, nickname, email);
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
            System.out.println("kakaoUser = " + kakaoUser.toString());

            memberRepository.save(kakaoUser);

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
    private void kakaoUsersAuthorizationInput(Authentication authentication, HttpServletResponse response) {
        UserDetailsImpl userDetailsImpl = ((UserDetailsImpl) authentication.getPrincipal());
        response.addCookie(new Cookie("accessToken", jwtProvider.createAccessToken(userDetailsImpl)));
        response.addCookie(new Cookie("refreshToken", jwtProvider.createRefreshToken(userDetailsImpl)));
    }
}