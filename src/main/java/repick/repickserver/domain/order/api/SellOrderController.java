package repick.repickserver.domain.order.api;


import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import repick.repickserver.domain.order.application.SellOrderService;
import repick.repickserver.domain.order.dto.SellOrderRequest;
import repick.repickserver.domain.order.dto.SellOrderResponse;

import java.util.List;

@RestController
@RequestMapping("/sell")
@RequiredArgsConstructor
public class SellOrderController {

    private final SellOrderService sellOrderService;

    @Operation(summary = "옷장 수거 내역 조회", description = "요청한 사용자의 처리되지 않은 판매 주문을 조회합니다.")
    @GetMapping(value = "/{state}")
    public ResponseEntity<List<SellOrderResponse>> getSellOrders(@PathVariable("state") String state, @RequestHeader("Authorization") String token) {
        return new ResponseEntity<>(sellOrderService.getSellOrders(state, token), HttpStatus.OK);
    }

    @Operation(summary = "옷장 수거 신청", description = "판매 주문을 요청합니다.")
    @PostMapping
    public ResponseEntity<Boolean> postSellOrder(@RequestBody SellOrderRequest request, @RequestHeader("Authorization") String token) {
        return new ResponseEntity<>(sellOrderService.postSellOrder(request, token), HttpStatus.OK);
    }

    @Operation(summary = "판매 주문 리스트 조회", description = "처리되지 않은 상태의 판매 주문 리스트를 조회합니다.")
    @GetMapping(value = "/admin/{state}")
    public ResponseEntity<List<SellOrderResponse>> getRequestedSellOrders(@PathVariable("state") String state) {
        return new ResponseEntity<>(sellOrderService.getRequestedSellOrders(state), HttpStatus.OK);
    }

    @Operation(summary = "옷장 수거 현황 변경", description = "판매 주문의 상태를 변경합니다.")
    @PostMapping(value = "/admin/update")
    public ResponseEntity<Boolean> updateSellOrder(@RequestBody SellOrderRequest request) {
        return new ResponseEntity<>(sellOrderService.updateSellOrder(request), HttpStatus.OK);
    }



}
