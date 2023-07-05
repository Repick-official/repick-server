package repick.repickserver.domain.member.controller;


import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import repick.repickserver.domain.member.application.MemberService;
import repick.repickserver.domain.member.dao.MemberRepository;
import repick.repickserver.domain.member.domain.Member;
import repick.repickserver.domain.member.dto.SignRequest;
import repick.repickserver.domain.member.dto.SignResponse;
import repick.repickserver.global.jwt.JwtProvider;

@RestController
@RequestMapping("/sign")
@RequiredArgsConstructor
public class SignController {

    private final MemberRepository memberRepository;
    private final MemberService memberService;
    private final JwtProvider jwtProvider;

    @PostMapping(value = "/login")
    public ResponseEntity<SignResponse> signin(@RequestBody SignRequest request) throws Exception {
        return new ResponseEntity<>(memberService.login(request), HttpStatus.OK);
    }

    @PostMapping(value = "/register")
    public ResponseEntity<Boolean> signup(@RequestBody SignRequest request) throws Exception {
        return new ResponseEntity<>(memberService.register(request), HttpStatus.OK);
    }

    @PatchMapping(value = "/update")
    public ResponseEntity<Boolean> update(@RequestBody SignRequest request, @RequestHeader("Authorization") String token) throws Exception {
        Member member = jwtProvider.getMember(token);
        return new ResponseEntity<>(memberService.update(request, member), HttpStatus.OK);
    }

    @GetMapping("/user/get")
    public ResponseEntity<SignResponse> getUser(@RequestParam String email) throws Exception {
        return new ResponseEntity<>( memberService.getMember(email), HttpStatus.OK);
    }

    @GetMapping("/admin/get")
    public ResponseEntity<SignResponse> getUserForAdmin(@RequestParam String email) throws Exception {
        return new ResponseEntity<>( memberService.getMember(email), HttpStatus.OK);
    }
}