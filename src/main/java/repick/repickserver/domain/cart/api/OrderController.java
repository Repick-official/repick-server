package repick.repickserver.domain.cart.api;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import repick.repickserver.domain.cart.application.OrderService;
import repick.repickserver.domain.cart.dto.OrderRequest;
import repick.repickserver.domain.cart.dto.OrderResponse;
import repick.repickserver.domain.cart.dto.OrderStateResponse;
import repick.repickserver.domain.cart.dto.UpdateOrderStateRequest;
import repick.repickserver.global.error.exception.CustomException;
import springfox.documentation.annotations.ApiIgnore;

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
}
