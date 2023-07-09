package repick.repickserver.domain.product.dto;

import lombok.*;
import repick.repickserver.domain.product.domain.Product;
import repick.repickserver.domain.product.domain.ProductImage;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RegisterProductResponse {

    private Long productId; // Product 의 PK
    private String name;
    private String detail;
    private Long price;
    private String size;
    private Long discountRate;
    // 이미지 정보
    private ImageFileInfo mainImageFile;
    private List<ImageFileInfo> detailImageFiles;

    @Builder
    public RegisterProductResponse(Product product, ProductImage mainProductImage, List<ProductImage> detailProductImages) {
        this.productId = product.getId();
        this.name = product.getName();
        this.detail = product.getDetail();
        this.price = product.getPrice();
        this.size = product.getSize();
        this.discountRate = product.getDiscountRate();
        this.mainImageFile = ImageFileInfo.builder()
                .imagePath(mainProductImage.getImagePath())
                .imageKey(mainProductImage.getImageKey())
                .isMainImage(true)
                .build();
        this.detailImageFiles = detailProductImages.stream()
                .map(detailProductImage -> ImageFileInfo.builder()
                        .imagePath(detailProductImage.getImagePath())
                        .imageKey(detailProductImage.getImageKey())
                        .isMainImage(false)
                        .build())
                .collect(Collectors.toList());
    }

    @Data
    @Builder
    public static class ImageFileInfo {
        private String imagePath;
        private String imageKey;
        private Boolean isMainImage;
    }
}