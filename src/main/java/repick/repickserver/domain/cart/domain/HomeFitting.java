package repick.repickserver.domain.cart.domain;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import repick.repickserver.domain.model.BaseTimeEntity;

import javax.persistence.*;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class HomeFitting extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="cart_product_id", unique = true)
    private CartProduct cartProduct;

    @Enumerated(EnumType.STRING)
    private HomeFittingState homeFittingState;

    @Builder
    public HomeFitting(CartProduct cartProduct) {
        this.cartProduct = cartProduct;
        this.homeFittingState = HomeFittingState.REQUESTED;
    }

    public void changeState(HomeFittingState homeFittingState) {
        this.homeFittingState = homeFittingState;
    }

    public void changeState(String homeFittingState) {
        this.homeFittingState = HomeFittingState.valueOf(homeFittingState);
    }
}
