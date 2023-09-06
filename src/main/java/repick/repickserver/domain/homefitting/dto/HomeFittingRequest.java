package repick.repickserver.domain.homefitting.dto;

import lombok.Getter;

import javax.validation.constraints.NotEmpty;
import java.util.List;

@Getter
public class HomeFittingRequest {
    @NotEmpty
    private List<Long> cartProductIds;
}
