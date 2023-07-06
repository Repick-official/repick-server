package repick.repickserver.domain.member.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import repick.repickserver.domain.member.application.SubscriberInfoService;
import repick.repickserver.domain.member.domain.Member;
import repick.repickserver.domain.member.dto.SubscriberInfoResponse;
import repick.repickserver.global.jwt.JwtProvider;

import java.util.List;

@RestController
@RequestMapping("/subscribe")
@RequiredArgsConstructor
public class SubscribeController {

    private final SubscriberInfoService subscriberInfoService;
    private final JwtProvider jwtProvider;

    @GetMapping("/check")
    public ResponseEntity<Boolean> check(@RequestHeader("Authorization") String token) throws Exception {
        Member member = jwtProvider.getMember(token);
        return new ResponseEntity<Boolean>(subscriberInfoService.check(member), HttpStatus.OK);
    }

    @GetMapping("/history")
    public ResponseEntity<List<SubscriberInfoResponse>> history(@RequestHeader("Authorization") String token) throws Exception {
        Member member = jwtProvider.getMember(token);
        return new ResponseEntity<List<SubscriberInfoResponse>>(subscriberInfoService.history(member), HttpStatus.OK);
    }
}
