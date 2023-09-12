package repick.repickserver.domain.product.domain;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import repick.repickserver.domain.model.BaseTimeEntity;
import repick.repickserver.domain.sellorder.domain.SellOrder;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;

import static repick.repickserver.domain.product.domain.ProductState.PREPARING;
import static repick.repickserver.domain.product.domain.ProductState.SELLING;

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

    private Long price;

    private String size;

    private Long discountRate;

    @Enumerated(EnumType.STRING)
    private ProductState productState;

    private String productNumber;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sell_order_id")
    private SellOrder sellOrder;

    @Builder
    public Product(String name, String detail, String brand, Long price, String size, Long discountRate, String productNumber, SellOrder sellOrder) {
        this.name = name;
        this.detail = detail;
        this.brand = brand;
        this.price = price;
        this.size = size;
        this.discountRate = discountRate;
        this.productState = PREPARING; // 230912 : changed from SELLING to PREPARING
        this.productNumber = productNumber;
        this.sellOrder = sellOrder;
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

    public void changePrice(Long price) {
        this.price = price;
    }

}
