package repick.repickserver.domain.product.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;

@Getter
@JsonFormat(shape = JsonFormat.Shape.STRING)
public enum ProductState {
    PREPARING, SELLING, PENDING, SOLD_OUT, DELETED,
    SETTLEMENT_REQUESTED, SETTLEMENT_COMPLETED // 정산 요청, 정산 완료
}
