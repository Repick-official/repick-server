package repick.repickserver.domain.product.dao;

import repick.repickserver.domain.cart.dto.GetHomeFittingResponse;
import repick.repickserver.domain.cart.dto.GetMyPickResponse;
import repick.repickserver.domain.product.dto.GetCategoryResponse;
import repick.repickserver.domain.product.dto.GetProductResponse;

import java.util.List;

public interface ProductRepositoryCustom {
    List<GetProductResponse> findPageByProductRegistrationDate(Long cursorId, Long categoryId, int pageSize);

    List<GetProductResponse> findPageByProductPriceDesc(Long cursorId, Long cursorPrice, Long categoryId, int pageSize);

    List<GetProductResponse> findPageByProductPriceAsc(Long cursorId, Long cursorPrice, Long categoryId, int pageSize);

    List<GetMyPickResponse> getMyPickProducts(Long cartId);

    List<GetHomeFittingResponse> getHomeFittingProducts(Long cartId);
}
