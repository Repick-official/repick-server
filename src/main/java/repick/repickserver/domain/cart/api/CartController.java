package repick.repickserver.domain.cart.api;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import repick.repickserver.domain.cart.application.CartService;
import repick.repickserver.domain.cart.dto.MyPickResponse;

@RestController
@RequestMapping("/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    @PostMapping("/my-pick/{productId}")
    public ResponseEntity<MyPickResponse> createMyPick(@PathVariable("productId") Long productId,
                                                       @RequestHeader("Authorization") String token) {
        return ResponseEntity.ok()
                .body(cartService.createMyPick(productId, token));
    }
}
