package repick.repickserver.domain.order.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;

@Getter
@JsonFormat(shape = JsonFormat.Shape.STRING)
public enum OrderCurrentState {
    // 미입금, 배송준비중(=입금완료), 배송중, 배송완료, 주문취소
    UNPAID, PREPARING, DELIVERING, DELIVERED, CANCELED
}
