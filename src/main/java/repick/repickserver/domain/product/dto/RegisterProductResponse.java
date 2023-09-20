package repick.repickserver.domain.product.dto;

import lombok.*;
import repick.repickserver.domain.product.domain.Product;
import repick.repickserver.domain.product.domain.ProductImage;
import repick.repickserver.domain.product.domain.ProductState;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RegisterProductResponse {

    private Long productId; // Product 의 PK
    private String name;
    private String detail;
    private String brand;
    private Long price;
    private String size;
    private Long discountRate;
    private ProductState productState;
    // 이미지 정보
    private ImageFileInfo mainImageFile;
    private List<ImageFileInfo> detailImageFiles;
    // 카테고리 정보
    private List<CategoryInfo> categoryInfoList;

    @Builder
    public RegisterProductResponse(Product product, ProductImage mainProductImage, List<ProductImage> detailProductImages, List<CategoryInfo> categoryInfoList) {
        this.productId = product.getId();
        this.name = product.getName();
        this.detail = product.getDetail();
        this.brand = product.getBrand();
        this.price = product.getPrice();
        this.size = product.getSize();
        this.discountRate = product.getDiscountRate();
        this.productState = product.getProductState();
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
        this.categoryInfoList = categoryInfoList;
    }

    @Data
    @Builder
    @NoArgsConstructor @AllArgsConstructor
    public static class ImageFileInfo {
        private String imagePath;
        private String imageKey;
        private Boolean isMainImage;
    }

    @Data
    @Builder
    public static class CategoryInfo {
        private Long categoryId;
        private String categoryName;
        private Long parentCategoryId;
        private String parentCategoryName;
    }
}
