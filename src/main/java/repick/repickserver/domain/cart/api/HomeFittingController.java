package repick.repickserver.domain.cart.api;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import repick.repickserver.domain.cart.application.HomeFittingService;
import repick.repickserver.domain.cart.dto.HomeFittingResponse;

@RestController
@RequestMapping("/home-fitting")
@RequiredArgsConstructor
public class HomeFittingController {

    private final HomeFittingService homeFittingService;

    /**
     * USER
     * 홈피팅 신청
     */
    @Operation(summary = "상품 홈피팅 신청", description = "상품 홈피팅을 신청합니다.")
    @PostMapping("/{cartProductId}")
    public ResponseEntity<HomeFittingResponse> requestHomeFitting(@PathVariable("cartProductId") Long cartProductId,
                                                                  @RequestHeader("Authorization") String token) {
        return ResponseEntity.ok()
                .body(homeFittingService.requestHomeFitting(cartProductId, token));
    }

}
