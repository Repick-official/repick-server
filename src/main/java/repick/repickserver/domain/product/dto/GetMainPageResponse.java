package repick.repickserver.domain.product.dto;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import repick.repickserver.domain.product.domain.Product;
import repick.repickserver.domain.product.domain.ProductImage;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class GetMainPageResponse {

    private Long productId; // Product 의 PK
    private String name;
    private String detail;
    private Long price;
    private String size;
    private Long discountRate;
    // 이미지 정보
    private RegisterProductResponse.ImageFileInfo mainImageFile;
    // TODO: View(조회수), Likes(좋아요수) 도 MVP 에서 구현하기로 결정되면 추가하기

    @Builder
    public GetMainPageResponse(Product product, ProductImage mainProductImage) {
        this.productId = product.getId();
        this.name = product.getName();
        this.detail = product.getDetail();
        this.price = product.getPrice();
        this.size = product.getSize();
        this.discountRate = product.getDiscountRate();
        this.mainImageFile = RegisterProductResponse.ImageFileInfo.builder()
                .imagePath(mainProductImage.getImagePath())
                .imageKey(mainProductImage.getImageKey())
                .isMainImage(true)
                .build();
    }
}
