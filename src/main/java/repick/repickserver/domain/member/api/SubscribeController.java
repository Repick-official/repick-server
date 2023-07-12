package repick.repickserver.domain.member.api;

import io.swagger.v3.oas.annotations.Operation;
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

    @Operation(summary = "구독 여부 조회", description = "요청한 유저 본인의 현재 구독 여부를 조회합니다.")
    @GetMapping("/check")
    public ResponseEntity<Boolean> check(@RequestHeader("Authorization") String token) throws Exception {
        return new ResponseEntity<>(subscriberInfoService.check(token), HttpStatus.OK);
    }

    @Operation(summary = "구독 기록 조회", description = "요청한 유저 본인의 구독 기록을 조회합니다.")
    @GetMapping("/history")
    public ResponseEntity<List<SubscriberInfoResponse>> history(@RequestHeader("Authorization") String token) throws Exception {
        return new ResponseEntity<>(subscriberInfoService.history(token), HttpStatus.OK);
    }

    @Operation(summary = "구독 승인", description = "관리자가 유저의 구독을 승인합니다. expireDate는 승인 후 한 달입니다.")
    @PostMapping("/admin/add")
    public ResponseEntity<Boolean> add(@RequestBody SubscriberInfoRequest request) {
        return new ResponseEntity<>(subscriberInfoService.add(request), HttpStatus.OK);
    }

    @Operation(summary = "구독 거절", description = "관리자가 유저의 구독을 거절합니다. expireDate는 null입니다.")
    @PostMapping("/admin/deny")
    public ResponseEntity<Boolean> deny(@RequestBody SubscriberInfoRequest request) {
        return new ResponseEntity<>(subscriberInfoService.deny(request), HttpStatus.OK);
    }

    @Operation(summary = "구독 요청", description = "유저가 구독을 요청합니다. expireDate는 7일입니다.")
    @PostMapping("/request")
    public ResponseEntity<Boolean> subscribeRequest(@RequestHeader("Authorization") String token) throws Exception {
        return new ResponseEntity<>(subscriberInfoService.subscribeRequest(token), HttpStatus.OK);
    }


}
