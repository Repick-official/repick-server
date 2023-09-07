package repick.repickserver.domain.product.dao;

import repick.repickserver.domain.homefitting.dto.GetHomeFittingResponse;
import repick.repickserver.domain.cart.dto.GetMyPickResponse;
import repick.repickserver.domain.member.domain.Member;
import repick.repickserver.domain.product.domain.Product;
import repick.repickserver.domain.product.domain.ProductState;
import repick.repickserver.domain.product.dto.GetProductResponse;

import java.util.List;

public interface ProductRepositoryCustom {
    List<GetProductResponse> findPageByProductRegistrationDate(Long cursorId, Long categoryId, int pageSize);

    List<GetProductResponse> findPageByProductPriceDesc(Long cursorId, Long cursorPrice, Long categoryId, int pageSize);

    List<GetProductResponse> findPageByProductPriceAsc(Long cursorId, Long cursorPrice, Long categoryId, int pageSize);

    List<GetMyPickResponse> getMyPickProducts(Long cartId);

    List<GetHomeFittingResponse> getHomeFittingProducts(Long cartId);

    List<GetHomeFittingResponse> getAllHomeFittingProducts(String homeFittingState);

    List<GetProductResponse> getSearchProducts(String keyword, Long cursorId, int pageSize);

    List<Product> findByMemberId(Long memberId);

    List<Product> findByMemberIdAndState(Long memberId, ProductState state);

    List<Product> findByMemberIdAndTwoStates(Long memberId, ProductState state1, ProductState state2);

    List<Product> findAllByIdListAndMember(List<Long> idList, Member member);

    List<GetProductResponse> getSearchProductsByPrice(String keyword, Long cursorId, Long cursorPrice, int pageSize, String sortType);

}
