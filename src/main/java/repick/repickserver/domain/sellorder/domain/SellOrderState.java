package repick.repickserver.domain.sellorder.domain;

import lombok.*;
import repick.repickserver.domain.model.BaseTimeEntity;

import javax.persistence.*;

@Entity
@Getter @Builder @AllArgsConstructor @NoArgsConstructor
public class SellOrderState extends BaseTimeEntity {

    @Id @GeneratedValue
    private Long id;

    @ManyToOne @JoinColumn(name = "sell_order_id")
    private SellOrder sellOrder;

    @Enumerated(EnumType.STRING)
    private SellState sellState;
}
