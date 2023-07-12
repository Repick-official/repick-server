package repick.repickserver.domain.member.api;


import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import repick.repickserver.domain.member.application.MemberService;
import repick.repickserver.domain.member.dto.SignRequest;
import repick.repickserver.domain.member.dto.SignResponse;

import javax.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/sign")
@RequiredArgsConstructor
public class SignController {

    private final MemberService memberService;

    @Operation(summary = "일반 로그인", description = "일반 로그인으로 토큰을 쿠키로 응답받습니다. 이메일과 비밀번호로 로그인합니다.")
    @PostMapping(value = "/login")
    public ResponseEntity<SignResponse> signin(@RequestBody SignRequest request, HttpServletResponse response) {
        return new ResponseEntity<>(memberService.login(request, response), HttpStatus.OK);
    }

    @Operation(summary = "일반 회원가입", description = "일반 회원가입을 처리합니다.")
    @PostMapping(value = "/register")
    public ResponseEntity<Boolean> signup(@RequestBody SignRequest request) {
        return new ResponseEntity<>(memberService.register(request), HttpStatus.OK);
    }

    @Operation(summary = "유저 정보 가져오기", description = "요청한 유저 본인의 개인정보를 가져옵니다.")
    @GetMapping(value = "/userInfo")
    public ResponseEntity<SignResponse> update(@RequestHeader("Authorization") String token) throws Exception {
        return new ResponseEntity<>(memberService.userInfo(token), HttpStatus.OK);
    }

    @Operation(summary = "유저 정보 수정하기", description = "유저의 개인정보를 수정합니다.")
    @PatchMapping(value = "/update")
    public ResponseEntity<Boolean> update(@RequestBody SignRequest request, @RequestHeader("Authorization") String token) throws Exception {
        return new ResponseEntity<>(memberService.update(request, token), HttpStatus.OK);
    }

    @GetMapping("/user/get")
    public ResponseEntity<SignResponse> getUser(@RequestParam String email) {
        return new ResponseEntity<>( memberService.getMember(email), HttpStatus.OK);
    }

    @GetMapping("/admin/get")
    public ResponseEntity<SignResponse> getUserForAdmin(@RequestParam String email) {
        return new ResponseEntity<>( memberService.getMember(email), HttpStatus.OK);
    }
}