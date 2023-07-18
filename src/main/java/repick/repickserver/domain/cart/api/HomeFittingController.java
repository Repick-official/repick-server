package repick.repickserver.domain.cart.api;

import io.swagger.v3.oas.annotations.Operation;
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
                .body(homeFittingService.requestHomeFitting(cartProductId));
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

}
