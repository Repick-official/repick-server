package repick.repickserver.domain.cart.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;

@Getter
@JsonFormat(shape = JsonFormat.Shape.STRING)
public enum CartProductState {
    IN_CART, HOME_FITTING_REQUESTED, ORDERED
}
