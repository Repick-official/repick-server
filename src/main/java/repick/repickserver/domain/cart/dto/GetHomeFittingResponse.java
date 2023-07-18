package repick.repickserver.domain.cart.dto;

import com.querydsl.core.annotations.QueryProjection;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import repick.repickserver.domain.cart.domain.HomeFittingState;
import repick.repickserver.domain.product.dto.GetProductResponse;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class GetHomeFittingResponse {

    // product
    private GetProductResponse product;
    // homeFitting
    private Long homeFittingId;
    private HomeFittingState homeFittingState;
    private LocalDateTime createdDate;
    private LocalDateTime lastModifiedDate;

    @QueryProjection
    @Builder
    public GetHomeFittingResponse(GetProductResponse product, Long homeFittingId, HomeFittingState homeFittingState,
                                  LocalDateTime createdDate, LocalDateTime lastModifiedDate) {
        this.product = product;
        this.homeFittingId = homeFittingId;
        this.homeFittingState = homeFittingState;
        this.createdDate = createdDate;
        this.lastModifiedDate = lastModifiedDate;
    }
}
