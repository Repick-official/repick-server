package repick.repickserver.domain.waybill.api;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import repick.repickserver.domain.waybill.application.WaybillNumberService;
import repick.repickserver.domain.waybill.dto.WaybillNumberRequest;
import repick.repickserver.domain.waybill.dto.WaybillNumberResponse;

@RestController
@RequestMapping("/waybill-number")
@RequiredArgsConstructor
public class WaybillNumberController {

    private final WaybillNumberService waybillNumberService;

    @Operation(summary = "운송장 번호 등록", description = "운송장 번호를 등록합니다.")
    @PostMapping("/register")
    public ResponseEntity<Boolean> postWaybillNumber(@RequestBody WaybillNumberRequest request) {
        return ResponseEntity.ok()
                .body(waybillNumberService.postWaybillNumber(request));
    }

    @Operation(summary = "운송장 번호 조회", description = "주문 번호로 운송장 번호를 조회합니다.")
    @GetMapping("/get")
    public ResponseEntity<WaybillNumberResponse> getWaybillNumber(@RequestParam String orderNumber) {
        return ResponseEntity.ok()
                .body(waybillNumberService.getWaybillNumber(orderNumber));
    }

    @Operation(summary = "운송장 번호 조회", description = "주문 번호로 운송장 번호를 조회합니다.")
    @GetMapping("/get-status")
    public ResponseEntity<String> getWaybillStatus(@RequestParam String orderNumber) throws JsonProcessingException {
        return ResponseEntity.ok()
                .body(waybillNumberService.getWaybillStatus(orderNumber));
    }

}
