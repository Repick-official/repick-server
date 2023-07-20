package repick.repickserver.domain.cart.dto;

import lombok.Getter;
import org.springframework.lang.Nullable;
import repick.repickserver.domain.cart.domain.OrderCurrentState;
import repick.repickserver.domain.model.Address;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

@Getter
public class OrderRequest {

    private Long memberId;

    private List<Long> productIds;

    @NotBlank
    private String personName;

    @NotBlank
    private String phoneNumber;

    @NotNull
    @Embedded
    private Address address;

    @Nullable
    private String requestDetail;

}
