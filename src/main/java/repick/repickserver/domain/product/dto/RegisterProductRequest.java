package repick.repickserver.domain.product.dto;

import lombok.Getter;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Getter
public class RegisterProductRequest {

    @NotBlank(message = "상품 이름을 입력해주세요.")
    private String name;

    private String detail;

    private String brand;

    @NotNull(message = "상품 가격을 입력해주세요.")
    private Long price;

    private String size;

    private Long discountRate;

}
