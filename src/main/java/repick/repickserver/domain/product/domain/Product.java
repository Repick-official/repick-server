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

    @NotNull
    private Long price;

    private String size;

    private Long discountRate;

    @Enumerated(EnumType.STRING)
    private ProductState productState;

    @Builder
    public Product(String name, String detail, Long price, String size, Long discountRate) {
        this.name = name;
        this.detail = detail;
        this.price = price;
        this.size = size;
        this.discountRate = discountRate;
        this.productState = SELLING;
    }

}
