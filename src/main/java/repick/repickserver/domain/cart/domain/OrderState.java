package repick.repickserver.domain.cart.domain;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class OrderState {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="order_id", nullable = false)
    private Order order;

    @Enumerated(EnumType.STRING)
    private OrderCurrentState orderCurrentState;

    public void setOrder(Order order) {
        this.order = order;
    }

    @Builder
    public OrderState(OrderCurrentState orderCurrentState) {
        this.orderCurrentState = orderCurrentState;
    }
}
