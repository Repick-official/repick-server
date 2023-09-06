package repick.repickserver.domain.order.domain;

import com.querydsl.core.annotations.QueryProjection;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import repick.repickserver.domain.model.BaseTimeEntity;

import javax.persistence.*;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class OrderState extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="order_id", nullable = false)
    private Order order;

    @Enumerated(EnumType.STRING)
    private OrderCurrentState orderCurrentState;

    @QueryProjection
    @Builder
    public OrderState(OrderCurrentState orderCurrentState, Order order) {
        this.order = order;
        this.orderCurrentState = orderCurrentState;
    }
}
