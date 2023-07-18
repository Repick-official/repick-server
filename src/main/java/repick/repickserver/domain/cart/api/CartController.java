package repick.repickserver.domain.cart.api;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import repick.repickserver.domain.cart.application.CartService;
import repick.repickserver.domain.cart.dto.GetMyPickResponse;
import repick.repickserver.domain.cart.dto.MyPickResponse;
import springfox.documentation.annotations.ApiIgnore;

import java.util.List;

@RestController
@RequestMapping("/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    @Operation(summary = "마이픽(장바구니) 담기", description = "마이픽(장바구니)에 상품을 담습니다.")
    @PostMapping("/my-pick/{productId}")
    public ResponseEntity<MyPickResponse> createMyPick(@PathVariable("productId") Long productId,
                                                       @ApiIgnore @RequestHeader("Authorization") String token) {
        return ResponseEntity.ok()
                .body(cartService.createMyPick(productId, token));
    }

    @Operation(summary = "마이픽(장바구니) 조회", description = "마이픽(장바구니)에 담긴 상품들을 조회합니다.")
    @GetMapping("/my-pick")
    public ResponseEntity<List<GetMyPickResponse>> getMyPick(@ApiIgnore @RequestHeader("Authorization") String token) {
        return ResponseEntity.ok()
                .body(cartService.getMyPick(token));
    }

}
