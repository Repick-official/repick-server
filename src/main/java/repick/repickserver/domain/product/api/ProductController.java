package repick.repickserver.domain.product.api;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import repick.repickserver.domain.product.application.ProductService;
import repick.repickserver.domain.product.dto.*;
import java.util.List;

@RestController
@RequestMapping("/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @PostMapping("/register")
    public ResponseEntity<RegisterProductResponse> registerProduct(@RequestPart("mainImageFile") MultipartFile mainImageFile,
                                                                   @RequestPart("detailImageFiles") List<MultipartFile> detailImageFiles,
                                                                   @RequestPart RegisterProductRequest request) {
        return ResponseEntity.ok()
                .body(productService.registerProduct(mainImageFile, detailImageFiles, request));
    }

    @Operation(summary = "상품 디테일 조회", description = "상품 디테일 product_id로 조회합니다.")
    @GetMapping("detail/{productId}")
    public ResponseEntity<RegisterProductResponse> getProductDetail(@PathVariable Long productId) {
        return ResponseEntity.ok()
                .body(productService.getProductDetail(productId));
    }

    // TODO: 메인페이지 추천상품 대용 임시 더미 데이터
    @GetMapping("/main-page")
    public ResponseEntity<List<RegisterProductResponse>> getMainPageProducts() {
        return ResponseEntity.ok()
                .body(productService.getMainDummyProducts());
    }
}
