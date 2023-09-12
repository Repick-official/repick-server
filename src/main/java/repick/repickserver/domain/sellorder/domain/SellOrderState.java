package repick.repickserver.domain.sellorder.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import repick.repickserver.domain.model.BaseTimeEntity;

import javax.persistence.*;

@Entity
@Getter @AllArgsConstructor @NoArgsConstructor
public class SellOrderState extends BaseTimeEntity {

    @Id @GeneratedValue
    private Long id;

    @ManyToOne @JoinColumn(name = "sell_order_id")
    private SellOrder sellOrder;

    @Enumerated(EnumType.STRING)
    private SellState sellState;

    @Builder
    private SellOrderState(SellOrder sellOrder, SellState sellState) {
        this.sellOrder = sellOrder;
        this.sellState = sellState;
    }

    public static SellOrderState of(SellOrder sellOrder, SellState sellState) {
        return SellOrderState.builder()
                .sellOrder(sellOrder)
                .sellState(sellState)
                .build();
    }
}
