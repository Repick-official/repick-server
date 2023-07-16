package repick.repickserver.domain.product.dao;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import repick.repickserver.domain.product.domain.ProductState;
import repick.repickserver.domain.product.dto.GetProductResponse;
import repick.repickserver.domain.product.dto.QGetProductResponse;

import java.util.List;
import java.util.stream.Collectors;

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

        return product.id.lt(cursorId).and(product.price.loe(cursorPrice));
    }

    private BooleanExpression cursorIdAndCursorPriceAsc(Long cursorId, Long cursorPrice) { // 첫 페이지 조회와 두번째 이상 페이지 조회를 구분하기 위함
        if(cursorId == null || cursorPrice == null) {
            return null;
        }

        return product.id.lt(cursorId).and(product.price.goe(cursorPrice));
    }

}
