package repick.repickserver.domain.product.application;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import repick.repickserver.domain.product.dao.CategoryRepository;
import repick.repickserver.domain.product.dto.GetCategoryResponse;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public List<GetCategoryResponse> getCategories() {
        return categoryRepository.findAll().stream()
                .map(category -> GetCategoryResponse.builder()
                        .id(category.getId())
                        .name(category.getName())
                        .parentCategory(category.getParentCategory())
                        .build())
                .collect(Collectors.toList());
    }

}
