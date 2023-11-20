package repick.repickserver.domain.order.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import repick.repickserver.domain.order.dto.OrderRequest;
import repick.repickserver.domain.order.dto.OrderResponse;
import repick.repickserver.domain.order.dto.OrderStateResponse;
import repick.repickserver.domain.order.dto.UpdateOrderStateRequest;
import repick.repickserver.domain.order.application.OrderService;
import repick.repickserver.global.error.exception.CustomException;
import springfox.documentation.annotations.ApiIgnore;

import java.util.List;

import static repick.repickserver.global.error.exception.ErrorCode.INVALID_REQUEST_ERROR;

@RestController
@RequestMapping("/order")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @Operation(summary = "상품 구매", description = "상품들을 구매합니다.")
    @PostMapping("/products")
    public ResponseEntity<OrderResponse> createOrder(@RequestBody OrderRequest orderRequest,
                                                     @ApiIgnore @RequestHeader("Authorization") String token) {
        return ResponseEntity.ok()
                .body(orderService.createOrder(orderRequest, token));
    }

    @Operation(summary = "주문 상태 변경 (Ex. 입금 확인 후)", description = "주문의 상태(미입금, 배송준비중(=입금완료), 배송중, 배송완료, 주문취소)를 변경합니다.")
    @PatchMapping("/admin/state")
    public ResponseEntity<OrderStateResponse> changeOrderState(@RequestBody UpdateOrderStateRequest request,
                                                               @ApiIgnore @RequestHeader("Authorization") String token) {
        // validation
        if(!request.getOrderState().equals("UNPAID") && !request.getOrderState().equals("PREPARING") && !request.getOrderState().equals("DELIVERING")
                && !request.getOrderState().equals("DELIVERED") && !request.getOrderState().equals("CANCELED"))
            throw new CustomException(INVALID_REQUEST_ERROR);

        return ResponseEntity.ok()
                .body(orderService.updateOrderState(request));
    }

    @Operation(summary = "전체 주문 상태 조회 (상태별로 필터링 가능)", description = "전체 주문의 상태(미입금, 배송준비중(=입금완료), 배송중, 배송완료, 주문취소)를 조회합니다.")
    @GetMapping("/admin/state")
    public ResponseEntity<List<OrderStateResponse>> getOrderStates(@Parameter(description = "UNPAID, PREPARING, DELIVERING, DELIVERED, CANCELED 중 하나를 요청, 없을시 전체 조회")
                                                    @RequestParam(value = "orderState", required = false) String orderState,
                                                    @ApiIgnore @RequestHeader("Authorization") String token) {
        if(orderState != null) {
            // validation
            if(!orderState.equals("UNPAID") && !orderState.equals("PREPARING") && !orderState.equals("DELIVERING")
                    && !orderState.equals("DELIVERED") && !orderState.equals("CANCELED"))
                throw new CustomException(INVALID_REQUEST_ERROR);
        }

        return ResponseEntity.ok()
                .body(orderService.getOrderStates(orderState));
    }

    @Operation(summary = "신규 주문 건 수")
    @GetMapping(value = "/admin/order-request-count")
    public ResponseEntity<Long> getOrderRequestCount() {
        return ResponseEntity.ok()
                .body(orderService.getOrderRequestCount());
    }

    @Operation(summary = "배송중 주문 건 수")
    @GetMapping(value = "/admin/delivering-order-count")
    public ResponseEntity<Long> getDeliveringOrderCount() {
        return ResponseEntity.ok()
                .body(orderService.getDeliveringOrderCount());
    }
}
