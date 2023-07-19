package repick.repickserver.domain.delivery.api;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import repick.repickserver.domain.delivery.application.DeliveryService;
import repick.repickserver.domain.delivery.dto.DeliveryRequest;
import repick.repickserver.domain.delivery.dto.DeliveryResponse;

@RestController
@RequestMapping("/delivery")
@RequiredArgsConstructor
public class DeliveryController {

    private final DeliveryService deliveryService;

    @Operation(summary = "운송장 번호 등록", description = "운송장 번호를 등록합니다.")
    @PostMapping("/admin/register")
    public ResponseEntity<Boolean> postWaybillNumber(@RequestBody DeliveryRequest request) {
        return ResponseEntity.ok()
                .body(deliveryService.postWaybillNumber(request));
    }

    @Operation(summary = "운송장 번호 조회", description = "주문 번호로 운송장 번호를 조회합니다.")
    @GetMapping("/get")
    public ResponseEntity<DeliveryResponse> getWaybillNumber(@RequestParam String orderNumber) {
        return ResponseEntity.ok()
                .body(deliveryService.getWaybillNumber(orderNumber));
    }

    @Operation(summary = "운송장 번호 조회", description = "주문 번호로 운송장 번호를 조회합니다.")
    @GetMapping("/get-status")
    public ResponseEntity<String> getWaybillStatus(@RequestParam String orderNumber) {
        return ResponseEntity.ok()
                .body(deliveryService.getWaybillStatus(orderNumber));
    }

}
