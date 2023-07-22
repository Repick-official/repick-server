package repick.repickserver.domain.product.domain;

import lombok.*;
import repick.repickserver.domain.model.BaseTimeEntity;
import javax.persistence.*;
import javax.validation.constraints.*;
import static repick.repickserver.domain.product.domain.ProductState.*;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Product extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    private String name;

    private String detail;

    private String brand;

    @NotNull
    private Long price;

    private String size;

    private Long discountRate;

    @Enumerated(EnumType.STRING)
    private ProductState productState;

    private String productNumber;

    @Builder
    public Product(String name, String detail, String brand, Long price, String size, Long discountRate, String productNumber) {
        this.name = name;
        this.detail = detail;
        this.brand = brand;
        this.price = price;
        this.size = size;
        this.discountRate = discountRate;
        this.productState = SELLING;
        this.productNumber = productNumber;
    }

    @Builder
    public Product(String name, String detail, String brand, Long price, String size, Long discountRate) {
        this.name = name;
        this.detail = detail;
        this.brand = brand;
        this.price = price;
        this.size = size;
        this.discountRate = discountRate;
        this.productState = SELLING;
    }

    public void changeProductState(ProductState productState) {
        this.productState = productState;
    }
}
