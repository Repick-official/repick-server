package repick.repickserver.domain.product.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import repick.repickserver.domain.product.application.CategoryService;
import repick.repickserver.domain.product.application.ProductService;
import repick.repickserver.domain.product.dto.FileVo;
import repick.repickserver.domain.product.dto.GetCategoryResponse;
import repick.repickserver.domain.product.dto.GetProductResponse;
import repick.repickserver.domain.product.dto.RegisterProductResponse;
import repick.repickserver.global.error.exception.CustomException;
import springfox.documentation.annotations.ApiIgnore;

import java.util.List;

import static repick.repickserver.global.error.exception.ErrorCode.INVALID_PRICE_SORT_TYPE;

@RestController
@RequestMapping("/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;
    private final CategoryService categoryService;

    @PostMapping(value = "/register", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<RegisterProductResponse> registerProduct(@ModelAttribute FileVo fileVo) {
        return ResponseEntity.ok()
                .body(productService.registerProduct(fileVo.getMainImageFile(), fileVo.getDetailImageFiles(),
                        fileVo.getRequest(), fileVo.getCategoryIds()));
    }

    @GetMapping("/category")
    public ResponseEntity<List<GetCategoryResponse>> getCategories() {
        return ResponseEntity.ok()
                .body(categoryService.getCategories());
    }

    @GetMapping("/main-page/recommendations")
    public ResponseEntity<List<GetProductResponse>> getMainPageProducts() {
        return ResponseEntity.ok()
                .body(productService.getMainPageProducts());
    }

    @Operation(summary = "상품 디테일 조회", description = "상품 디테일 product_id로 조회합니다.")
    @GetMapping("detail/{productId}")
    public ResponseEntity<RegisterProductResponse> getProductDetail(@PathVariable Long productId) {
        return ResponseEntity.ok()
                .body(productService.getProductDetail(productId));
    }

    @Operation(summary = "전체/카테고리 상품 보기 (최신순)", description = "최신순으로 상품 리스트 페이지를 조회합니다.")
    @GetMapping("/latest")
    public ResponseEntity<List<GetProductResponse>> getLatestProducts(@Parameter(description = "1번째 페이지 조회시 null, " +
                          "2번째 이상 페이지 조회시 직전 페이지의 마지막 product id") @RequestParam(required = false) Long cursorId,
                          @Parameter(description = "전체 조회시 null, " +
                          "특정 카테고리(Ex. 티셔츠) 조회시 category id") @RequestParam(required = false) Long categoryId,
                          @Parameter(description = "한 페이지에 가져올 상품 개수") @RequestParam int pageSize) {
        return ResponseEntity.ok()
                .body(productService.getPageByProductRegistrationDate(cursorId, categoryId, pageSize));
    }

    @Operation(summary = "전체/카테고리 상품 보기 (가격 높은순)", description = "가격 높은순으로 상품 리스트 페이지를 조회합니다.")
    @GetMapping("/price-highest")
    public ResponseEntity<List<GetProductResponse>> getPriceHighestProducts(@Parameter(description = "1번째 페이지 조회시 null, " +
                        "2번째 이상 페이지 조회시 직전 페이지의 마지막 product id") @RequestParam(required = false) Long cursorId,
                        @Parameter(description = "1번째 페이지 조회시 null, " +
                        "2번째 이상 페이지 조회시 직전 페이지의 마지막 product price") @RequestParam(required = false) Long cursorPrice,
                        @Parameter(description = "전체 조회시 null, " +
                        "특정 카테고리(Ex. 티셔츠) 조회시 category id") @RequestParam(required = false) Long categoryId,
                        @Parameter(description = "한 페이지에 가져올 상품 개수") @RequestParam int pageSize) {
        return ResponseEntity.ok()
                .body(productService.getPageByProductPriceDesc(cursorId, cursorPrice, categoryId, pageSize));
    }

    @Operation(summary = "전체/카테고리 상품 보기 (가격 낮은순)", description = "가격 낮은순으로 상품 리스트 페이지를 조회합니다.")
    @GetMapping("/price-lowest")
    public ResponseEntity<List<GetProductResponse>> getPriceLowestProducts(@Parameter(description = "1번째 페이지 조회시 null, " +
            "2번째 이상 페이지 조회시 직전 페이지의 마지막 product id") @RequestParam(required = false) Long cursorId,
            @Parameter(description = "1번째 페이지 조회시 null, " +
            "2번째 이상 페이지 조회시 직전 페이지의 마지막 product price") @RequestParam(required = false) Long cursorPrice,
            @Parameter(description = "전체 조회시 null, " +
            "특정 카테고리(Ex. 티셔츠) 조회시 category id") @RequestParam(required = false) Long categoryId,
            @Parameter(description = "한 페이지에 가져올 상품 개수") @RequestParam int pageSize) {
        return ResponseEntity.ok()
                .body(productService.getPageByProductPriceAsc(cursorId, cursorPrice, categoryId, pageSize));
    }

    @Operation(summary = "키워드로 상품 검색 (상품명, 브랜드명) - 최신순", description = "키워드로 상품 리스트 페이지를 조회합니다. (검색 결과가 없을 시 빈 리스트가 반환됩니다.)")
    @GetMapping("/keyword")
    public ResponseEntity<List<GetProductResponse>> getSearchProducts(@Parameter(description = "1번째 페이지 조회시 null, " +
                                                "2번째 이상 페이지 조회시 직전 페이지의 마지막 product id") @RequestParam(required = false) Long cursorId,
                                                @Parameter(description = "한 페이지에 가져올 상품 개수") @RequestParam int pageSize,
                                                                      @Parameter(description = "검색 키워드") @RequestParam String keyword) {
        return ResponseEntity.ok()
                .body(productService.getPageByKeyword(keyword, cursorId, pageSize));
    }

    @Operation(summary = "키워드로 상품 검색 (상품명, 브랜드명) - 가격높은/낮은순", description = "키워드로 상품 리스트 페이지를 조회합니다. (검색 결과가 없을 시 빈 리스트가 반환됩니다.)")
    @GetMapping("/keyword/by-price")
    public ResponseEntity<List<GetProductResponse>> getSearchProductsByPrice(@Parameter(description = "1번째 페이지 조회시 null, " +
            "2번째 이상 페이지 조회시 직전 페이지의 마지막 product id") @RequestParam(required = false) Long cursorId,
                                                                             @Parameter(description = "1번째 페이지 조회시 null, " +
            "2번째 이상 페이지 조회시 직전 페이지의 마지막 product price") @RequestParam(required = false) Long cursorPrice,
                                                                      @Parameter(description = "한 페이지에 가져올 상품 개수") @RequestParam int pageSize,
                                                                      @Parameter(description = "검색 키워드") @RequestParam String keyword,
                                                                      @Parameter(description = "가격높은순이면 high, 가격낮은순이면 low 로 요청") @RequestParam String sortType) {
        if(!sortType.equals("high") && !sortType.equals("low")) {
            throw new CustomException(INVALID_PRICE_SORT_TYPE);
        }

        return ResponseEntity.ok()
                .body(productService.getPageByKeywordSortByPrice(keyword, cursorId, cursorPrice, pageSize, sortType));
    }

    // 230912 : 추가됨
    @Operation(summary = "상품 가격 입력")
    @PostMapping("/submit-price")
    public ResponseEntity<Boolean> submitPrice(@Parameter(description = "상품 id") @RequestParam Long productId,
                                                          @Parameter(description = "상품 가격") @RequestParam Long price,
                                                          @ApiIgnore @RequestHeader("Authorization") String token) {
        return ResponseEntity.ok()
                .body(productService.submitPrice(productId, price, token));
    }

}
