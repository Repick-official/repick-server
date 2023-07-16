package repick.repickserver.domain.product.dto;

import com.querydsl.core.annotations.QueryProjection;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import repick.repickserver.domain.product.domain.Product;
import repick.repickserver.domain.product.domain.ProductImage;
import repick.repickserver.domain.product.domain.ProductState;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class GetProductResponse {

    private Long productId; // Product 의 PK
    private String name;
    private String detail;
    private String brand;
    private Long price;
    private String size;
    private Long discountRate;
    private ProductState productState;
    // 이미지 정보
    private RegisterProductResponse.ImageFileInfo mainImageFile;
    // TODO: View(조회수), Likes(좋아요수) 도 MVP 에서 구현하기로 결정되면 추가하기

    @Builder
    @QueryProjection
    public GetProductResponse(Product product, ProductImage mainProductImage) {
        this.productId = product.getId();
        this.name = product.getName();
        this.detail = product.getDetail();
        this.brand = product.getBrand();
        this.price = product.getPrice();
        this.size = product.getSize();
        this.discountRate = product.getDiscountRate();
        this.productState = product.getProductState();
        this.mainImageFile = RegisterProductResponse.ImageFileInfo.builder()
                .imagePath(mainProductImage.getImagePath())
                .imageKey(mainProductImage.getImageKey())
                .isMainImage(true)
                .build();
    }
}
