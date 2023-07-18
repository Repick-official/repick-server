package repick.repickserver.domain.cart.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;

@Getter
@JsonFormat(shape = JsonFormat.Shape.STRING)
public enum HomeFittingState {
    REQUESTED, DELIVERING, DELIVERED, RETURN_REQUESTED, RETURNED, PURCHASED
}
