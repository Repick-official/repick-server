package repick.repickserver.domain.product.dto;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import repick.repickserver.domain.product.domain.Category;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class GetCategoryResponse {

    private Long id;
    private String name;
    private Long parentId;
    private String parentName;

    @Builder
    public GetCategoryResponse(Long id, String name, Long parentId, String parentName) {
        this.id = id;
        this.name = name;
        this.parentId = parentId;
        this.parentName = parentName;
    }
}
