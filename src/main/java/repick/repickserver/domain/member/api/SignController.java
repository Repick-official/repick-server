package repick.repickserver.domain.member.api;


import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import repick.repickserver.domain.member.application.MemberService;
import repick.repickserver.domain.member.dto.SignLoginRequest;
import repick.repickserver.domain.member.dto.SignLoginResponse;
import repick.repickserver.domain.member.dto.SignUpdateRequest;
import repick.repickserver.domain.member.dto.SignUserInfoResponse;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/sign")
@RequiredArgsConstructor
public class SignController {

    private final MemberService memberService;

    @Operation(summary = "일반 로그인", description = "일반 로그인으로 토큰을 쿠키로 응답받습니다. 이메일과 비밀번호로 로그인합니다.")
    @PostMapping(value = "/login")
    public ResponseEntity<SignLoginResponse> signin(@RequestBody SignLoginRequest request,
                                                    HttpServletResponse response) {
        return ResponseEntity.ok()
                .body(memberService.login(request, response));
    }

    @Operation(summary = "토큰 리프레쉬", description = "리프레쉬 토큰을 받아 새로운 토큰을 줍니다.")
    @PostMapping(value = "/refresh")
    public ResponseEntity<String> refresh(@RequestBody String token) {
        return ResponseEntity.ok()
                .body(memberService.refresh(token));
    }

    @Operation(summary = "일반 회원가입", description = "일반 회원가입을 처리합니다.")
    @PostMapping(value = "/register")
    public ResponseEntity<SignUserInfoResponse> signup(@RequestBody SignUpdateRequest request) {
        return ResponseEntity.ok()
                .body(memberService.register(request));
    }

    @Operation(summary = "유저 정보 가져오기", description = "요청한 유저 본인의 개인정보를 가져옵니다.")
    @GetMapping(value = "/userInfo")
    public ResponseEntity<SignUserInfoResponse> getUserInfo(@ApiIgnore @RequestHeader("Authorization") String token) {
        return ResponseEntity.ok()
                .body(memberService.getUserInfo(token));
    }

    @Operation(summary = "유저 정보 수정하기", description = "유저의 개인정보를 수정합니다." +
            "\n\nnull 값이 들어오면 기존의 값으로 유지됩니다.")
    @PatchMapping(value = "/update")
    public ResponseEntity<SignUserInfoResponse> update(@RequestBody SignUpdateRequest request,
                                          @ApiIgnore @RequestHeader("Authorization") String token) {
        return ResponseEntity.ok()
                .body(memberService.update(request, token));
    }

    /*

    !! DEPRECATED !!
    개발용으로 더이상 사용하지 않는 메서드입니다.

    @GetMapping("/user/get")
    public ResponseEntity<SignResponse> getUser(@RequestParam String email) {
        return ResponseEntity.ok()
                .body(memberService.getMember(email));
    }

    @GetMapping("/admin/get")
    public ResponseEntity<SignResponse> getUserForAdmin(@RequestParam String email) {
        return ResponseEntity.ok()
                .body(memberService.getMember(email));
    }
    */
}