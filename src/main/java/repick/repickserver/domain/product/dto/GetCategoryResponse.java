package repick.repickserver.domain.product.dto;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class GetCategoryResponse {

    private Long id;
    private String name;

    @Builder
    public GetCategoryResponse(Long id, String name) {
        this.id = id;
        this.name = name;
    }
}
