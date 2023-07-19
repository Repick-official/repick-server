package repick.repickserver.domain.product.dao;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import repick.repickserver.domain.cart.domain.HomeFittingState;
import repick.repickserver.domain.cart.dto.GetHomeFittingResponse;
import repick.repickserver.domain.cart.dto.GetMyPickResponse;
import repick.repickserver.domain.cart.dto.QGetHomeFittingResponse;
import repick.repickserver.domain.cart.dto.QGetMyPickResponse;
import repick.repickserver.domain.product.domain.ProductState;
import repick.repickserver.domain.product.dto.GetProductResponse;
import repick.repickserver.domain.product.dto.QGetProductResponse;
import java.util.List;
import java.util.stream.Collectors;
import static repick.repickserver.domain.cart.domain.CartProductState.HOME_FITTING_REQUESTED;
import static repick.repickserver.domain.cart.domain.CartProductState.IN_CART;
import static repick.repickserver.domain.cart.domain.QCartProduct.cartProduct;
import static repick.repickserver.domain.cart.domain.QHomeFitting.homeFitting;
import static repick.repickserver.domain.product.domain.QProduct.product;
import static repick.repickserver.domain.product.domain.QProductCategory.productCategory;
import static repick.repickserver.domain.product.domain.QProductImage.productImage;

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
     * 상품의 상태(CartProductState) 가 IN_CART 인 상품만 조회
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
                        product.productState.eq(ProductState.SELLING),
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
                        product.productState.eq(ProductState.SELLING),
                        productImage.isMainImage.eq(true),
                        cartProduct.cartProductState.eq(HOME_FITTING_REQUESTED))
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

}
