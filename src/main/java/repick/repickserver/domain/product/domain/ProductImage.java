package repick.repickserver.domain.product.domain;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class ProductImage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    private String imagePath;

    @NotBlank
    private String imageKey;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @NotNull
    private Boolean isMainImage;

    @Builder
    public ProductImage(String imagePath, String imageKey, Product product, Boolean isMainImage) {
        this.imagePath = imagePath;
        this.imageKey = imageKey;
        this.product = product;
        this.isMainImage = isMainImage;
    }

}
