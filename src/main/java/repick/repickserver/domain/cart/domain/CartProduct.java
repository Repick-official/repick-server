package repick.repickserver.domain.cart.domain;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import repick.repickserver.domain.model.BaseTimeEntity;
import repick.repickserver.domain.product.domain.Product;

import javax.persistence.*;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class CartProduct extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cart_id", nullable = false)
    private Cart cart;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Enumerated(EnumType.STRING)
    private CartProductState cartProductState;

    @Builder
    public CartProduct(Cart cart, Product product) {
        this.cart = cart;
        this.product = product;
        this.cartProductState = CartProductState.IN_CART;
    }
}
