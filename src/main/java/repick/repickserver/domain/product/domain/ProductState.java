package repick.repickserver.domain.product.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;

@Getter
@JsonFormat(shape = JsonFormat.Shape.STRING)
public enum ProductState {
    SELLING, SOLD_OUT, DELETED
}
