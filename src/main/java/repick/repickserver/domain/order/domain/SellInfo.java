package repick.repickserver.domain.order.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.lang.Nullable;
import repick.repickserver.domain.model.BaseTimeEntity;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Getter
@Builder
@Entity
@AllArgsConstructor
@NoArgsConstructor
public class SellInfo extends BaseTimeEntity {

    @Id @GeneratedValue
    private Long id;

    @NotNull
    @OneToOne
    @JoinColumn(name = "order_id")
    private Order order;

    @NotNull
    private Integer bagQuantity;

    @NotNull
    private Integer productQuantity;

    @NotNull
    private LocalDateTime returnDate;

    // 계좌 은행, 번호
    @NotNull
    private String bankName;

    @NotNull
    private String accountNumber;

    private SellState sellState;

}
