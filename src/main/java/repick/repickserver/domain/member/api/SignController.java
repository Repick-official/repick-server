package repick.repickserver.domain.member.api;


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

    @PostMapping(value = "/login")
    public ResponseEntity<SignResponse> signin(@RequestBody SignRequest request, HttpServletResponse response) {
        return new ResponseEntity<>(memberService.login(request, response), HttpStatus.OK);
    }

    @PostMapping(value = "/register")
    public ResponseEntity<Boolean> signup(@RequestBody SignRequest request) {
        return new ResponseEntity<>(memberService.register(request), HttpStatus.OK);
    }

    @GetMapping(value = "/userInfo")
    public ResponseEntity<SignResponse> update(@RequestHeader("Authorization") String token) throws Exception {
        return new ResponseEntity<>(memberService.userInfo(token), HttpStatus.OK);
    }

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