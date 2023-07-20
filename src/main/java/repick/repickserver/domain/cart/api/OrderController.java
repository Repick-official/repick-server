package repick.repickserver.domain.cart.api;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import repick.repickserver.domain.cart.application.OrderService;
import repick.repickserver.domain.cart.dto.OrderRequest;
import repick.repickserver.domain.cart.dto.OrderResponse;
import springfox.documentation.annotations.ApiIgnore;

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
}
