package repick.repickserver.domain.user.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import repick.repickserver.domain.user.application.KakaoUserService;
import repick.repickserver.domain.user.dto.SocialUserInfoDto;

import javax.servlet.http.HttpServletResponse;

@RestController
public class OauthController {

    @Autowired
    KakaoUserService kakaoUserService;

    // 카카오 로그인
    @GetMapping("/oauth/kakao")
    public SocialUserInfoDto kakaoLogin(@RequestParam String code, HttpServletResponse response) throws JsonProcessingException {
        System.out.println("code = " + code);
        return kakaoUserService.kakaoLogin(code, response);
    }

}
