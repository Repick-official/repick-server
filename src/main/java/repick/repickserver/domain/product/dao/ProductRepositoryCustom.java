package repick.repickserver.domain.product.dao;

import repick.repickserver.domain.product.dto.GetProductResponse;

import java.util.List;

public interface ProductRepositoryCustom {
    List<GetProductResponse> findPageByProductRegistrationDate(Long cursorId, Long categoryId, int pageSize);

    List<GetProductResponse> findPageByProductPriceDesc(Long cursorId, Long cursorPrice, Long categoryId, int pageSize);
}
