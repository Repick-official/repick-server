package repick.repickserver.domain.sellorder.api;


import io.swagger.annotations.ApiImplicitParam;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import repick.repickserver.domain.product.domain.ProductState;
import repick.repickserver.domain.sellorder.application.SellOrderService;
import repick.repickserver.domain.sellorder.dto.*;
import repick.repickserver.domain.product.dto.GetProductResponse;
import springfox.documentation.annotations.ApiIgnore;

import java.util.List;

@RestController
@RequestMapping("/sell")
@RequiredArgsConstructor
public class SellOrderController {

    private final SellOrderService sellOrderService;

    @Operation(summary =  "정산 상태 업데이트", description = "정산 상태를 업데이트 합니다.")
    @PostMapping(value = "/settlement/admin/update")
    public ResponseEntity<Boolean> updateSettlementState(@RequestBody UpdateSettlementStateRequest request) {
        return ResponseEntity.ok()
                .body(sellOrderService.updateSettlementState(request));
    }

    @Operation(summary = "정산 신청", description = "정산 신청을 합니다.")
    @PostMapping(value = "/settlement")
    public ResponseEntity<Boolean> settlement(@ApiIgnore @RequestHeader("Authorization") String token,
                                                         @RequestBody SettlementRequest settlementRequest) {
        return ResponseEntity.ok()
                .body(sellOrderService.requestSettlement(token, settlementRequest));
    }


    @Operation(summary = "옷장 정리 현황: 전체보기", description = "옷장 정리 현황을 전체보기로 조회합니다.")
    @GetMapping(value = "/history/published")
    public ResponseEntity<List<GetProductResponse>> getAllProductByMember(@ApiIgnore @RequestHeader("Authorization") String token) {
        return ResponseEntity.ok()
                .body(sellOrderService.getAllProductByMember(token));
    }

    @Operation(summary = "옷장 정리 현황: 판매준비")
    @GetMapping(value = "/history/preparing")
    public ResponseEntity<List<GetProductResponse>> getPreparingSellOrders (@ApiIgnore @RequestHeader("Authorization") String token) {
        return ResponseEntity.ok()
                .body(sellOrderService.getProductByProductState(token, ProductState.BEFORE_SMS, ProductState.PREPARING));
    }

    @Operation(summary = "옷장 정리 현황: 판매중만", description = "옷장 정리 현황을 판매중만으로 조회합니다.")
    @GetMapping(value = "/history/selling")
    public ResponseEntity<List<GetProductResponse>> getSellingSellOrders (@ApiIgnore @RequestHeader("Authorization") String token) {
        return ResponseEntity.ok()
                .body(sellOrderService.getProductByProductState(token, ProductState.SELLING, ProductState.PENDING));
    }

    @Operation(summary = "옷장 정리 현황: 판매완료", description = "옷장 정리 현황을 판매완료만으로 조회합니다.")
    @GetMapping(value = "/history/sold")
    public ResponseEntity<List<GetProductResponse>> getSoldSellOrders (@ApiIgnore @RequestHeader("Authorization") String token) {
        return ResponseEntity.ok()
                .body(sellOrderService.getProductByProductState(token, ProductState.SOLD_OUT));
    }

    @Operation(summary = "옷장 정리 현황: 정산됨", description = "옷장 정리 현황을 정산됨만으로 조회합니다.")
    @GetMapping(value = "/history/settled")
    public ResponseEntity<List<GetProductResponse>> getSettledSellOrders (@ApiIgnore @RequestHeader("Authorization") String token) {
        return ResponseEntity.ok()
                .body(sellOrderService.getProductByProductState(token, ProductState.SETTLEMENT_REQUESTED, ProductState.SETTLEMENT_COMPLETED));
    }

    @Operation(summary = "판매 주문 현황", description = "판매 주문 현황을 조회합니다.")
    @GetMapping(value = "/history/requests")
    public ResponseEntity<List<SellOrderResponse>> getSellOrderRequests(@ApiIgnore @RequestHeader("Authorization") String token) {
        return ResponseEntity.ok()
                .body(sellOrderService.getAllSellOrders(token));
    }

    @Operation(summary = "판매 주문 현황: 상태로 필터링", description = "주문 상태로 판매 주문을 조회합니다.")
    @GetMapping(value = "/history/requests/{state}")
    @ApiImplicitParam(
            name = "state",
            value = "신청 상태 (requested | bag_pending | bag_ready | canceled | delivered | published)",
            required = true,
            dataType = "String",
            paramType = "path",
            defaultValue = "None"
    )
    public ResponseEntity<List<SellOrderResponse>> getSellOrders(@PathVariable("state") String state,
                                                                 @ApiIgnore @RequestHeader("Authorization") String token) {
        return ResponseEntity.ok()
                .body(sellOrderService.getSellOrders(state, token));
    }

    @Operation(summary = "옷장 수거 신청", description = "판매 주문을 요청합니다.")
    @PostMapping
    public ResponseEntity<SellOrderResponse> postSellOrder(@RequestBody SellOrderRequest request,
                                                 @ApiIgnore @RequestHeader("Authorization") String token) {
        return ResponseEntity.ok()
                .body(sellOrderService.postSellOrder(request, token));
    }

    @Operation(summary = "요청중인 판매 등록 리스트 조회", description = "요청중인 판매 등록 리스트를 조회합니다")
    @ApiImplicitParam(
            name = "state",
            value = "신청 상태 (requested | bag_pending | bag_ready | canceled | delivered | published)",
            required = true,
            dataType = "String",
            paramType = "path",
            defaultValue = "None"
    )
    @GetMapping(value = "/admin/{state}")
    public ResponseEntity<List<SellOrderResponse>> getAllSellOrders(@PathVariable("state") String state) {
        return ResponseEntity.ok()
                .body(sellOrderService.getAllSellOrdersAdmin(state));
    }

    @Operation(summary = "옷장 수거 현황 변경", description = "판매 주문의 상태를 변경합니다.")
    @PostMapping(value = "/admin/update")
    public ResponseEntity<SellOrderResponse> updateSellOrder(@RequestBody SellOrderUpdateRequest request) {
        return ResponseEntity.ok()
                .body(sellOrderService.updateSellOrderState(request));
    }

    @Operation(summary = "옷장 정리 신규 신청 건 수")
    @GetMapping(value = "/admin/bag-request-count")
    public ResponseEntity<Long> getBagRequestCount() {
        return ResponseEntity.ok()
                .body(sellOrderService.getBagRequestCount());
    }

    @Operation(summary = "정산 대기 건 수")
    @GetMapping(value = "/admin/settlement-request-count")
    public ResponseEntity<Long> getSettlementRequestCount() {
        return ResponseEntity.ok()
                .body(sellOrderService.getSettlementRequestCount());
    }

}
