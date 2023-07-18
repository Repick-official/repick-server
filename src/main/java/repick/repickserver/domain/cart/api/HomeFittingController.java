package repick.repickserver.domain.cart.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import repick.repickserver.domain.cart.application.HomeFittingService;
import repick.repickserver.domain.cart.dto.GetHomeFittingResponse;
import repick.repickserver.domain.cart.dto.HomeFittingResponse;
import springfox.documentation.annotations.ApiIgnore;
import java.util.List;

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
                                                                  @ApiIgnore @RequestHeader("Authorization") String token) {
        return ResponseEntity.ok()
                .body(homeFittingService.requestHomeFitting(cartProductId, token));
    }

    /**
     * USER
     * 본인의 홈피팅 상품 조회
     */
    @Operation(summary = "홈피팅 상품 조회", description = "본인의 홈피팅 상품을 조회합니다.")
    @GetMapping("")
    public ResponseEntity<List<GetHomeFittingResponse>> getMyHomeFitting(@ApiIgnore @RequestHeader("Authorization") String token) {
        return ResponseEntity.ok()
                .body(homeFittingService.getMyHomeFitting(token));
    }

     /**
     * ADMIN
     * 전체 또는 특정 상태의 홈피팅 상품 조회 (홈피팅 상태: REQUESTED, DELIVERING, DELIVERED, RETURNED, PURCHASED)
     */
    @Operation(summary = "홈피팅 상품 조회 (관리자)", description = "홈피팅 상품들을 상태별로 필터링하여 조회합니다.")
    @GetMapping("/admin")
    public ResponseEntity<List<GetHomeFittingResponse>> getHomeFitting(@Parameter(description = "REQUESTED, DELIVERING, " +
            "DELIVERED, RETURNED, PURCHASED 중 하나를 요청, 없을시 전체 조회") @RequestParam(value = "homeFittingState", required = false) String homeFittingState,
                                                               @ApiIgnore @RequestHeader("Authorization") String token) {
        return ResponseEntity.ok()
                .body(homeFittingService.getHomeFitting(homeFittingState));
    }

}
