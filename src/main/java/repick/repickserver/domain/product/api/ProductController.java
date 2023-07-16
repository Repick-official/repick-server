package repick.repickserver.domain.product.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import repick.repickserver.domain.product.application.CategoryService;
import repick.repickserver.domain.product.application.ProductService;
import repick.repickserver.domain.product.dto.*;
import java.util.List;

@RestController
@RequestMapping("/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;
    private final CategoryService categoryService;

    @PostMapping("/register")
    public ResponseEntity<RegisterProductResponse> registerProduct(@RequestPart("mainImageFile") MultipartFile mainImageFile,
                                                                   @RequestPart("detailImageFiles") List<MultipartFile> detailImageFiles,
                                                                   @RequestPart RegisterProductRequest request) {
        return ResponseEntity.ok()
                .body(productService.registerProduct(mainImageFile, detailImageFiles, request));
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

    @Operation(summary = "전체/카테고리 상품 보기 (가격 높은순)", description = "가격 낮은순으로 상품 리스트 페이지를 조회합니다.")
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
}
