package repick.repickserver.domain.member.api;

import io.swagger.annotations.ApiImplicitParam;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import repick.repickserver.domain.member.application.SubscriberInfoService;
import repick.repickserver.domain.member.dto.SubscriberInfoRegisterRequest;
import repick.repickserver.domain.member.dto.SubscriberInfoRequest;
import repick.repickserver.domain.member.dto.SubscriberInfoResponse;
import springfox.documentation.annotations.ApiIgnore;

import java.util.List;

@RestController
@RequestMapping("/subscribe")
@RequiredArgsConstructor
public class SubscribeController {

    private final SubscriberInfoService subscriberInfoService;

    @Operation(summary = "구독 여부 조회", description = "요청한 유저 본인의 현재 구독 여부를 조회합니다.")
    @GetMapping("/check")
    public ResponseEntity<String> check(@ApiIgnore @RequestHeader("Authorization") String token) {
        return ResponseEntity.ok()
                .body(subscriberInfoService.check(token));
    }

    @Operation(summary = "구독 기록 조회", description = "요청한 유저 본인의 구독 기록을 조회합니다.\n" +
            "\nexpired: 승인 후 만료된 구독(유효기간 1달)\n" +
            "\nrequest-expired: 요청 만료된 구독 (입금 대기기간 7일)")
    @ApiImplicitParam(
            name = "state",
            value = "구독 상태 (requested | approved | denied | expired | request-expired)",
            required = true,
            dataType = "String",
            paramType = "path",
            defaultValue = "None"
    )
    @GetMapping("/history/{state}")
    public ResponseEntity<List<SubscriberInfoResponse>> history(@PathVariable("state") String state,
                                                                @ApiIgnore @RequestHeader("Authorization") String token) {
        return ResponseEntity.ok()
                .body(subscriberInfoService.history(state, token));
    }

    @Operation(summary = "구독 요청", description = "유저가 구독을 요청합니다. expireDate는 7일입니다." +
            "\n\n 구독 종류는 다음과 같습니다 : BASIC, PRO, PREMIUM")
    @PostMapping("/request")
    public ResponseEntity<SubscriberInfoResponse> subscribeRequest(@ApiIgnore @RequestHeader("Authorization") String token,
                                                    @RequestBody SubscriberInfoRegisterRequest request) {
        return ResponseEntity.ok()
                .body(subscriberInfoService.subscribeRequest(token, request));
    }

    @Operation(summary = "요청된 구독 조회", description = "관리자가 모든 유저의 처리되지 않은 구독 요청을 조회합니다. 만료된 요청은 보이지 않습니다.")
    @GetMapping("/admin/requested")
    public ResponseEntity<List<SubscriberInfoResponse>> getRequestedSubscriberInfos() {
        return ResponseEntity.ok()
                .body(subscriberInfoService.getRequestedSubscriberInfos());
    }

    @Operation(summary = "구독 승인", description = "관리자가 유저의 구독을 승인합니다. expireDate는 승인 후 한 달입니다.")
    @PostMapping("/admin/add")
    public ResponseEntity<SubscriberInfoResponse> add(@RequestBody SubscriberInfoRequest request) {
        return ResponseEntity.ok()
                .body(subscriberInfoService.add(request));
    }

    @Operation(summary = "구독 거절", description = "관리자가 유저의 구독을 거절합니다. expireDate는 null입니다.")
    @PostMapping("/admin/deny")
    public ResponseEntity<SubscriberInfoResponse> deny(@RequestBody SubscriberInfoRequest request) {
        return ResponseEntity.ok()
                .body(subscriberInfoService.deny(request));
    }

}
