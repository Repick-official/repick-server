package repick.repickserver.domain.cart.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import repick.repickserver.domain.cart.application.HomeFittingService;
import repick.repickserver.domain.cart.dto.GetHomeFittingResponse;
import repick.repickserver.domain.cart.dto.HomeFittingRequest;
import repick.repickserver.domain.cart.dto.HomeFittingResponse;
import repick.repickserver.global.error.exception.CustomException;
import springfox.documentation.annotations.ApiIgnore;
import java.util.List;

import static repick.repickserver.global.error.exception.ErrorCode.INVALID_REQUEST_ERROR;

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
    @PostMapping("")
    public ResponseEntity<List<HomeFittingResponse>> requestHomeFitting(@RequestBody HomeFittingRequest homeFittingRequest,
                                                                  @ApiIgnore @RequestHeader("Authorization") String token) {
        return ResponseEntity.ok()
                .body(homeFittingService.requestHomeFitting(homeFittingRequest, token));
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
     * 전체 또는 특정 상태의 홈피팅 상품 조회 (홈피팅 상태: REQUESTED, DELIVERING, DELIVERED, RETURN_REQUESTED, RETURNED, PURCHASED)
     */
    @Operation(summary = "홈피팅 상품 조회 (관리자)", description = "홈피팅 상품들을 상태별로 필터링하여 조회합니다.")
    @GetMapping("/admin")
    public ResponseEntity<List<GetHomeFittingResponse>> getHomeFitting(@Parameter(description = "REQUESTED, DELIVERING, " +
                                                                        "DELIVERED, RETURN_REQUESTED, RETURNED, PURCHASED 중 하나를 요청, 없을시 전체 조회")
                                                                        @RequestParam(value = "homeFittingState", required = false) String homeFittingState,
                                                               @ApiIgnore @RequestHeader("Authorization") String token) {
        if(homeFittingState != null) {
            // validation
            if (!homeFittingState.equals("REQUESTED") && !homeFittingState.equals("DELIVERING") && !homeFittingState.equals("DELIVERED")
                    && !homeFittingState.equals("RETURN_REQUESTED") && !homeFittingState.equals("RETURNED") && !homeFittingState.equals("PURCHASED"))
                throw new CustomException(INVALID_REQUEST_ERROR);
        }

        return ResponseEntity.ok()
                .body(homeFittingService.getHomeFitting(homeFittingState));
    }

    /**
     * ADMIN
     * 홈피팅 상품의 상태를 변경
     */
    @Operation(summary = "홈피팅 상품 상태 변경 (관리자)", description = "홈피팅 상품의 상태를 변경합니다.")
    @PatchMapping("/admin/{homeFittingId}")
    public ResponseEntity<Void> changeHomeFittingState(@PathVariable("homeFittingId") Long homeFittingId,
                                                       @Parameter(description = "REQUESTED, DELIVERING, " +
                                                        "DELIVERED, RETURN_REQUESTED(반품 요청), RETURNED(반품 완료), PURCHASED 중 하나를 요청")
                                                       @RequestParam("homeFittingState") String homeFittingState,
                                                       @ApiIgnore @RequestHeader("Authorization") String token) {
        // validation
        if (!homeFittingState.equals("REQUESTED") && !homeFittingState.equals("DELIVERING") && !homeFittingState.equals("DELIVERED")
                && !homeFittingState.equals("RETURN_REQUESTED") && !homeFittingState.equals("RETURNED") && !homeFittingState.equals("PURCHASED"))
            throw new CustomException(INVALID_REQUEST_ERROR);

        homeFittingService.changeHomeFittingState(homeFittingId, homeFittingState);
        return ResponseEntity.ok().build();
    }

}
