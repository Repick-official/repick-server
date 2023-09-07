package repick.repickserver.domain.product.dao;

import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.SubQueryExpression;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import repick.repickserver.domain.homefitting.domain.HomeFittingState;
import repick.repickserver.domain.homefitting.dto.GetHomeFittingResponse;
import repick.repickserver.domain.cart.dto.GetMyPickResponse;
import repick.repickserver.domain.cart.dto.QGetMyPickResponse;
import repick.repickserver.domain.homefitting.dto.QGetHomeFittingResponse;
import repick.repickserver.domain.member.domain.Member;
import repick.repickserver.domain.product.domain.Product;
import repick.repickserver.domain.product.domain.ProductState;
import repick.repickserver.domain.product.dto.GetProductResponse;
import repick.repickserver.domain.product.dto.QGetProductResponse;
import repick.repickserver.global.error.exception.CustomException;

import java.util.List;
import java.util.stream.Collectors;

import static repick.repickserver.domain.cart.domain.CartProductState.HOME_FITTING_REQUESTED;
import static repick.repickserver.domain.cart.domain.CartProductState.IN_CART;
import static repick.repickserver.domain.cart.domain.QCartProduct.cartProduct;
import static repick.repickserver.domain.homefitting.domain.QHomeFitting.homeFitting;
import static repick.repickserver.domain.product.domain.QProduct.product;
import static repick.repickserver.domain.product.domain.QProductCategory.productCategory;
import static repick.repickserver.domain.product.domain.QProductImage.productImage;
import static repick.repickserver.global.error.exception.ErrorCode.INVALID_REQUEST_ERROR;

@RequiredArgsConstructor
public class ProductRepositoryImpl implements ProductRepositoryCustom {

    private final JPAQueryFactory jpaQueryFactory;

    /**
     * 최신순으로 전체 또는 특정 카테고리의 상품 조회
     * No Offset Pagination (페이징 성능 향상)
     */
    public List<GetProductResponse> findPageByProductRegistrationDate(Long cursorId, Long categoryId, int pageSize) {

        return jpaQueryFactory
                .select(new QGetProductResponse(
                        product,
                        productImage))
                .from(product)
                .leftJoin(productImage)
                .on(productImage.product.id.eq(product.id))
                .leftJoin(productCategory)
                .on(productCategory.product.id.eq(product.id))
                .where(ltProductId(cursorId),
                        filterByCategory(categoryId),
                        categoryIdEq(categoryId),
                        product.productState.eq(ProductState.SELLING),
                        productImage.isMainImage.eq(true))
                .orderBy(product.id.desc())
                .limit(pageSize)
                .fetch()
                .stream()
                .distinct()
                .collect(Collectors.toList());
    }

    /**
     * 가격높은순으로 전체 또는 특정 카테고리의 상품 조회
     * No Offset Pagination (페이징 성능 향상)
     */
    public List<GetProductResponse> findPageByProductPriceDesc(Long cursorId, Long cursorPrice, Long categoryId,
                                                               int pageSize) {

        return jpaQueryFactory
                .select(new QGetProductResponse(
                        product,
                        productImage))
                .from(product)
                .leftJoin(productImage)
                .on(productImage.product.id.eq(product.id))
                .leftJoin(productCategory)
                .on(productCategory.product.id.eq(product.id))
                .where(cursorIdAndCursorPriceDesc(cursorId, cursorPrice),
                        filterByCategory(categoryId),
                        categoryIdEq(categoryId),
                        product.productState.eq(ProductState.SELLING),
                        productImage.isMainImage.eq(true))
                .orderBy(product.price.desc(), product.id.desc())
                .limit(pageSize)
                .fetch()
                .stream()
                .distinct()
                .collect(Collectors.toList());
    }

    /**
     * 가격낮은순으로 전체 또는 특정 카테고리의 상품 조회
     * No Offset Pagination (페이징 성능 향상)
     */
    public List<GetProductResponse> findPageByProductPriceAsc(Long cursorId, Long cursorPrice, Long categoryId,
                                                               int pageSize) {

        return jpaQueryFactory
                .select(new QGetProductResponse(
                        product,
                        productImage))
                .from(product)
                .leftJoin(productImage)
                .on(productImage.product.id.eq(product.id))
                .leftJoin(productCategory)
                .on(productCategory.product.id.eq(product.id))
                .where(cursorIdAndCursorPriceAsc(cursorId, cursorPrice),
                        filterByCategory(categoryId),
                        categoryIdEq(categoryId),
                        product.productState.eq(ProductState.SELLING),
                        productImage.isMainImage.eq(true))
                .orderBy(product.price.asc(), product.id.desc())
                .limit(pageSize)
                .fetch()
                .stream()
                .distinct()
                .collect(Collectors.toList());
    }

    /**
     * 마이픽 상품 조회
     * 상품의 상태(CartProductState) 가 IN_CART 인 상품만 조회 (홈피팅 신청한 상품은 제외)
     */
    public List<GetMyPickResponse> getMyPickProducts(Long cartId) {
        return jpaQueryFactory
                .select(new QGetMyPickResponse(
                        new QGetProductResponse(product, productImage),
                        cartProduct.id,
                        cartProduct.cartProductState))
                .from(product)
                .leftJoin(productImage)
                .on(productImage.product.id.eq(product.id))
                .leftJoin(cartProduct)
                .on(cartProduct.product.id.eq(product.id))
                .where(cartProduct.cart.id.eq(cartId),
                        product.productState.eq(ProductState.SELLING),
                        productImage.isMainImage.eq(true),
                        cartProduct.cartProductState.in(IN_CART))
                .fetch()
                .stream()
                .distinct()
                .collect(Collectors.toList());
    }

    /**
     * 홈피팅 상품 조회
     * 상품의 상태(CartProductState) 가 HOME_FITTING_REQUESTED 인 상품만 조회
     */
    public List<GetHomeFittingResponse> getHomeFittingProducts(Long cartId) {
        return jpaQueryFactory
                .select(new QGetHomeFittingResponse(
                        new QGetProductResponse(product, productImage),
                        homeFitting.id,
                        homeFitting.homeFittingState,
                        homeFitting.createdDate,
                        homeFitting.lastModifiedDate))
                .from(product)
                .leftJoin(productImage)
                .on(productImage.product.id.eq(product.id))
                .leftJoin(cartProduct)
                .on(cartProduct.product.id.eq(product.id))
                .leftJoin(homeFitting)
                .on(homeFitting.cartProduct.id.eq(cartProduct.id))
                .where(cartProduct.cart.id.eq(cartId),
                        product.productState.eq(ProductState.PENDING),
                        productImage.isMainImage.eq(true),
                        cartProduct.cartProductState.eq(HOME_FITTING_REQUESTED))
                .fetch()
                .stream()
                .distinct()
                .collect(Collectors.toList());
    }

    /**
     * 홈피팅 상품 전체 조회 (관리자)
     * 홈피팅 상태로 필터링 가능
     */
    public List<GetHomeFittingResponse> getAllHomeFittingProducts(String homeFittingState) {
        return jpaQueryFactory
                .select(new QGetHomeFittingResponse(
                        new QGetProductResponse(product, productImage),
                        homeFitting.id,
                        homeFitting.homeFittingState,
                        homeFitting.createdDate,
                        homeFitting.lastModifiedDate))
                .from(product)
                .leftJoin(productImage)
                .on(productImage.product.id.eq(product.id))
                .leftJoin(cartProduct)
                .on(cartProduct.product.id.eq(product.id))
                .leftJoin(homeFitting)
                .on(homeFitting.cartProduct.id.eq(cartProduct.id))
                .where(homeFittingStateEq(homeFittingState),
                        product.productState.eq(ProductState.PENDING),
                        productImage.isMainImage.eq(true),
                        cartProduct.cartProductState.eq(HOME_FITTING_REQUESTED))
                .fetch()
                .stream()
                .distinct()
                .collect(Collectors.toList());
    }

    /**
     * 상품 키워드 검색 (최신순)
     * 검색한 키워드를 포함하는 상품명 또는 브랜드명 을 가진 상품 조회
     */
    public List<GetProductResponse> getSearchProducts(String keyword, Long cursorId, int pageSize) {
        return jpaQueryFactory
                .select(new QGetProductResponse(
                        product,
                        productImage))
                .from(product)
                .leftJoin(productImage)
                .on(productImage.product.id.eq(product.id))
                .where(product.name.contains(keyword).or(product.brand.contains(keyword)),
                        ltProductId(cursorId),
                        product.productState.eq(ProductState.SELLING),
                        productImage.isMainImage.eq(true))
                .orderBy(product.id.desc())
                .limit(pageSize)
                .fetch()
                .stream()
                .distinct()
                .collect(Collectors.toList());
    }

    @Override
    public List<Product> findByMemberId(Long memberId) {
        return jpaQueryFactory.selectFrom(product)
                .where(product.sellOrder.member.id.eq(memberId))
                .fetch();
    }

    @Override
    public List<Product> findByMemberIdAndState(Long memberId, ProductState state) {
        return jpaQueryFactory.selectFrom(product)
                // product의 sellorder의 memberId가 memberId와 같은 것만 조회
                .where(product.sellOrder.member.id.eq(memberId)
                        // product state가 state와 같은 것들로 조회
                        .and(product.productState.eq(state)))
                .fetch();
    }

    @Override
    public List<Product> findByMemberIdAndTwoStates(Long memberId, ProductState state1, ProductState state2) {
        return jpaQueryFactory.selectFrom(product)
                // product의 sellorder의 memberId가 memberId와 같은 것만 조회
                .where(product.sellOrder.member.id.eq(memberId)
                        // product state가 state1과 state2와 같은 것들로 조회
                        .and(product.productState.eq(state1).or(product.productState.eq(state2))))
                .fetch();
    }

    @Override
    public List<Product> findAllByIdListAndMember(List<Long> idList, Member member) {
        return jpaQueryFactory.selectFrom(product)
                .where(product.id.in(idList)
                .and(product.sellOrder.member.eq(member)))
                .fetch();
    }

    /**
     * 상품 키워드 검색 (가격높은순 / 가격낮은순)
     * 검색한 키워드를 포함하는 상품명 또는 브랜드명 을 가진 상품 조회
     */
    public List<GetProductResponse> getSearchProductsByPrice(String keyword, Long cursorId, Long cursorPrice, int pageSize, String sortType) {
        return jpaQueryFactory
                .select(new QGetProductResponse(
                        product,
                        productImage))
                .from(product)
                .leftJoin(productImage)
                .on(productImage.product.id.eq(product.id))
                .where(product.name.contains(keyword).or(product.brand.contains(keyword)),
                        cursorIdAndCursorPriceType(sortType, cursorId, cursorPrice),
                        product.productState.eq(ProductState.SELLING),
                        productImage.isMainImage.eq(true))
                .orderBy(orderByPriceType(sortType), product.id.desc())
                .limit(pageSize)
                .fetch()
                .stream()
                .distinct()
                .collect(Collectors.toList());

    }

    private BooleanExpression ltProductId(Long cursorId) { // 첫 페이지 조회와 두번째 이상 페이지 조회를 구분하기 위함
        return cursorId != null ? product.id.lt(cursorId) : null;
    }

    private BooleanExpression categoryIdEq(Long categoryId) { // 카테고리가 없는 경우 전체 상품 조회
        return categoryId != null ? productCategory.category.id.eq(categoryId) : null;
    }

    // 각 productCategory 별 가장 최신의 productCategory id를 구함
    SubQueryExpression<Long> maxProductCategoryIdSubQuery = JPAExpressions
            .select(productCategory.id.max())
            .from(productCategory)
            .groupBy(productCategory.product.id);

    private BooleanExpression filterByCategory(Long categoryId) {
        return categoryId == null ? productCategory.id.in(maxProductCategoryIdSubQuery) : null;
    }

    private BooleanExpression cursorIdAndCursorPriceDesc(Long cursorId, Long cursorPrice) { // 첫 페이지 조회와 두번째 이상 페이지 조회를 구분하기 위함
        if(cursorId == null || cursorPrice == null) {
            return null;
        }

        return product.price.eq(cursorPrice)
                .and(product.id.lt(cursorId))
                .or(product.price.lt(cursorPrice));
    }

    private BooleanExpression cursorIdAndCursorPriceAsc(Long cursorId, Long cursorPrice) { // 첫 페이지 조회와 두번째 이상 페이지 조회를 구분하기 위함
        if(cursorId == null || cursorPrice == null) {
            return null;
        }

        return product.price.eq(cursorPrice)
                .and(product.id.lt(cursorId))
                .or(product.price.gt(cursorPrice));
    }

    private BooleanExpression homeFittingStateEq(String homeFittingState) {
        return homeFittingState != null ? homeFitting.homeFittingState.eq(HomeFittingState.valueOf(homeFittingState)) : null;
    }

    private OrderSpecifier<Long> orderByPriceType(String sortType) {
        if(sortType.equals("high")) {
            return product.price.desc();
        }
        else if (sortType.equals("low")) {
            return product.price.asc();
        }
        else throw new CustomException(INVALID_REQUEST_ERROR);
    }

    private BooleanExpression cursorIdAndCursorPriceType(String sortType, Long cursorId, Long cursorPrice) {
        if(sortType.equals("high")) {
            return cursorIdAndCursorPriceDesc(cursorId, cursorPrice);
        }
        else if (sortType.equals("low")) {
            return cursorIdAndCursorPriceAsc(cursorId, cursorPrice);
        }
        else throw new CustomException(INVALID_REQUEST_ERROR);
    }
}
