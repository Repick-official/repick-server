package repick.repickserver.domain.product.api;

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
}
