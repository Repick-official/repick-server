package repick.repickserver.domain.member.api;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import repick.repickserver.domain.member.application.KakaoUserService;
import repick.repickserver.domain.member.dto.SocialUserInfoDto;

import javax.servlet.http.HttpServletResponse;

@RestController
@RequiredArgsConstructor
public class OauthController {

    private final KakaoUserService kakaoUserService;

    // 카카오 로그인
    @Operation(summary = "카카오 로그인", description = "카카오 로그인으로 토큰을 쿠키로 응답받습니다.")
    @GetMapping("/oauth/kakao")
    public SocialUserInfoDto kakaoLogin(@Parameter(name = "code", description = "카카오 로그인을 위한 코드", required = true)
                                        @RequestParam String code,
                                        @RequestParam String redirect_uri,
                                        HttpServletResponse response) throws JsonProcessingException {
        System.out.println("code = " + code);
        return kakaoUserService.kakaoLogin(code, redirect_uri, response);
    }

}
