package repick.repickserver.domain.member.api;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import repick.repickserver.domain.member.application.SubscriberInfoService;
import repick.repickserver.domain.member.dto.SubscriberInfoRequest;
import repick.repickserver.domain.member.dto.SubscriberInfoResponse;

import java.util.List;

@RestController
@RequestMapping("/subscribe")
@RequiredArgsConstructor
public class SubscribeController {

    private final SubscriberInfoService subscriberInfoService;

    @GetMapping("/check")
    public ResponseEntity<Boolean> check(@RequestHeader("Authorization") String token) throws Exception {
        return new ResponseEntity<>(subscriberInfoService.check(token), HttpStatus.OK);
    }

    @GetMapping("/history")
    public ResponseEntity<List<SubscriberInfoResponse>> history(@RequestHeader("Authorization") String token) throws Exception {
        return new ResponseEntity<>(subscriberInfoService.history(token), HttpStatus.OK);
    }

    @PostMapping("/add")
    public ResponseEntity<Boolean> add(@RequestBody SubscriberInfoRequest request) {
        return new ResponseEntity<>(subscriberInfoService.add(request), HttpStatus.OK);
    }

    @PostMapping("/deny")
    public ResponseEntity<Boolean> deny(@RequestBody SubscriberInfoRequest request) {
        return new ResponseEntity<>(subscriberInfoService.deny(request), HttpStatus.OK);
    }

    @PostMapping("/request")
    public ResponseEntity<Boolean> subscribeRequest(@RequestHeader("Authorization") String token) throws Exception {
        return new ResponseEntity<>(subscriberInfoService.subscribeRequest(token), HttpStatus.OK);
    }


}
