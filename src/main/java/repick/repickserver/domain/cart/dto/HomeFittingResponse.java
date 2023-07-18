package repick.repickserver.domain.cart.dto;

import com.querydsl.core.annotations.QueryProjection;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.LastModifiedDate;
import repick.repickserver.domain.cart.domain.HomeFitting;
import repick.repickserver.domain.cart.domain.HomeFittingState;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class HomeFittingResponse {

    private Long HomeFittingId;
    private Long cartProductId;
    private HomeFittingState homeFittingState;
    private LocalDateTime createdDate;
    private LocalDateTime lastModifiedDate;

    @QueryProjection
    @Builder
    public HomeFittingResponse(HomeFitting homeFitting) {
        this.HomeFittingId = homeFitting.getId();
        this.cartProductId = homeFitting.getCartProduct().getId();
        this.homeFittingState = homeFitting.getHomeFittingState();
        this.createdDate = homeFitting.getCreatedDate();
        this.lastModifiedDate = homeFitting.getLastModifiedDate();
    }
}
