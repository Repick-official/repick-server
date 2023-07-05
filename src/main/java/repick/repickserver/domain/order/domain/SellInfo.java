package repick.repickserver.domain.order.domain;

import javax.persistence.*;

@Entity
public class SellInfo {

    @Id @GeneratedValue
    private Long id;

    @OneToOne
    @JoinColumn(name = "order_id")
    private Order order;

}
