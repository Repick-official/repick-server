package repick.repickserver.domain.homefitting.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;

@Getter
@JsonFormat(shape = JsonFormat.Shape.STRING)
public enum HomeFittingState {
    // 홈피팅 요청, 배송중, 배송 완료, 반품 요청, 반품 완료, 구매 완료
    REQUESTED, DELIVERING, DELIVERED, RETURN_REQUESTED, RETURNED, PURCHASED
}
