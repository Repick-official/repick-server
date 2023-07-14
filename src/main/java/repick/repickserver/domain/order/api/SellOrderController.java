package repick.repickserver.domain.order.api;


import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import repick.repickserver.domain.order.application.SellOrderService;
import repick.repickserver.domain.order.domain.SellOrder;
import repick.repickserver.domain.order.dto.SellOrderRequest;
import repick.repickserver.domain.order.dto.SellOrderResponse;
import springfox.documentation.annotations.ApiIgnore;

import java.util.List;

@RestController
@RequestMapping("/sell")
@RequiredArgsConstructor
public class SellOrderController {

    private final SellOrderService sellOrderService;

    @Operation(summary = "옷장 수거 내역", description = "주문의 id로 판매 주문을 조회합니다.")
    @GetMapping(value = "/{state}")
    public ResponseEntity<List<SellOrderResponse>> getSellOrders(@PathVariable("state") String state,
                                                                 @ApiIgnore @RequestHeader("Authorization") String token) {
        return ResponseEntity.ok()
                .body(sellOrderService.getSellOrders(state, token));
    }

    @Operation(summary = "옷장 수거 신청", description = "판매 주문을 요청합니다.")
    @PostMapping
    public ResponseEntity<Boolean> postSellOrder(@RequestBody SellOrderRequest request,
                                                 @ApiIgnore @RequestHeader("Authorization") String token) {
        return ResponseEntity.ok()
                .body(sellOrderService.postSellOrder(request, token));
    }

    @Operation(summary = "요청중인 판매 등록 리스트 조회", description = "요청중인 판매 등록 리스트를 조회합니다")
    @GetMapping(value = "/admin/requested")
    public ResponseEntity<List<SellOrderResponse>> getSellOrders() {
        return ResponseEntity.ok()
                .body(sellOrderService.getRequestedSellOrders());
    }

    @Operation(summary = "옷장 수거 현황 변경", description = "판매 주문의 상태를 변경합니다.")
    @PostMapping(value = "/admin/update")
    public ResponseEntity<SellOrder> updateSellOrder(@RequestBody SellOrderRequest request) {
        return ResponseEntity.ok()
                .body(sellOrderService.updateSellOrder(request));
    }



}
