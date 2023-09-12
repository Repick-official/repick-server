package repick.repickserver.domain.product.application;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import repick.repickserver.domain.member.domain.Member;
import repick.repickserver.domain.product.validator.ProductValidator;
import repick.repickserver.domain.sellorder.repository.SellOrderRepository;
import repick.repickserver.domain.sellorder.domain.SellOrder;
import repick.repickserver.domain.ordernumber.application.OrderNumberService;
import repick.repickserver.domain.product.dao.CategoryRepository;
import repick.repickserver.domain.product.dao.ProductCategoryRepository;
import repick.repickserver.domain.product.dao.ProductImageRepository;
import repick.repickserver.domain.product.dao.ProductRepository;
import repick.repickserver.domain.product.domain.*;
import repick.repickserver.domain.product.dto.GetProductResponse;
import repick.repickserver.domain.product.dto.RegisterProductRequest;
import repick.repickserver.domain.product.dto.RegisterProductResponse;
import repick.repickserver.global.error.exception.CustomException;
import repick.repickserver.global.jwt.JwtProvider;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import static repick.repickserver.global.error.exception.ErrorCode.*;
import static repick.repickserver.domain.product.domain.ProductState.*;

@Service
@Transactional
@RequiredArgsConstructor
public class ProductService {

    private final AwsS3Service awsS3Service;
    private final ProductRepository productRepository;
    private final ProductImageRepository productImageRepository;
    private final CategoryRepository categoryRepository;
    private final ProductCategoryRepository productCategoryRepository;
    private final OrderNumberService orderNumberService;
    private final SellOrderRepository sellOrderRepository;
    private final ObjectMapper objectMapper;
    private final JwtProvider jwtProvider;
    private final ProductValidator productValidator;

    public RegisterProductResponse registerProduct(MultipartFile mainImageFile, List<MultipartFile> detailImageFiles,
                                                   String requestString, List<Long> categoryIds) {

        RegisterProductRequest request;
        try {
            request = objectMapper.readValue(requestString, RegisterProductRequest.class);
        } catch (Exception e) {
            throw new CustomException(REQUEST_BODY_INVALID);
        }


        // sellOrderNumber로 SellOrder 가져오고 없으면 에러
        Optional<SellOrder> sellOrder = sellOrderRepository.findByOrderNumber(request.getSellOrderNumber());
        if (sellOrder.isEmpty()) {
            throw new CustomException(ORDER_NUMBER_NOT_FOUND);
        }

        // S3 에 메인 이미지 업로드
        AwsS3 awsS3Main;
        try {
            awsS3Main = awsS3Service.upload(mainImageFile, "main");
        } catch (Exception e) {
            throw new CustomException(S3_UPLOAD_FAIL);
        }

        // S3에 상세 이미지들 업로드
        List<AwsS3> awsS3Details = new ArrayList<>();
        for (MultipartFile detailImageFile : detailImageFiles) {
            try {
                AwsS3 awsS3Detail = awsS3Service.upload(detailImageFile, "detail");
                awsS3Details.add(awsS3Detail);
            } catch (Exception e) {
                throw new CustomException(S3_UPLOAD_FAIL);
            }
        }

        // 상품 고유번호 생성
        String productNumber = orderNumberService.generateProductNumber();

        // 상품 정보 저장
        Product product = Product.builder()
                .name(request.getName())
                .detail(request.getDetail())
                .brand(request.getBrand())
                .size(request.getSize())
                .productNumber(productNumber)
                .sellOrder(sellOrder.get())
                .build();

        Product savedProduct = productRepository.save(product);

        // 메인 이미지 저장
        ProductImage mainProductImage = ProductImage.builder()
                .imagePath(awsS3Main.getPath())
                .imageKey(awsS3Main.getKey())
                .product(product)
                .isMainImage(true)
                .build();

        ProductImage savedMainProductImage = productImageRepository.save(mainProductImage);

        // 상세 이미지들 저장
        List<ProductImage> savedDetailProductImages = awsS3Details.stream()
                .map(awsS3Detail -> ProductImage.builder()
                        .imagePath(awsS3Detail.getPath())
                        .imageKey(awsS3Detail.getKey())
                        .product(product)
                        .isMainImage(false)
                        .build())
                .map(productImageRepository::save)
                .collect(Collectors.toList());

        // 상품 카테고리 저장
        List<ProductCategory> productCategories = categoryIds.stream()
                .map(categoryId -> categoryRepository.findById(categoryId)
                        .orElseThrow(() -> new CustomException(INVALID_CATEGORY_ID)))
                .map(category -> new ProductCategory(savedProduct, category))
                .collect(Collectors.toList());

        productCategoryRepository.saveAll(productCategories);

        List<RegisterProductResponse.CategoryInfo> categoryInfoList = productCategories.stream()
                .map(ProductCategory::getCategory)
                .map(this::mapCategoryToCategoryInfo)
                .collect(Collectors.toList());


        return RegisterProductResponse.builder()
                .product(savedProduct)
                .mainProductImage(savedMainProductImage)
                .detailProductImages(savedDetailProductImages)
                .categoryInfoList(categoryInfoList)
                .build();
    }

    private RegisterProductResponse.CategoryInfo mapCategoryToCategoryInfo(Category category) {
        Long parentId = category.getParentCategory() != null ? category.getParentCategory().getId() : null;
        String parentName = category.getParentCategory() != null ? category.getParentCategory().getName() : null;
        return RegisterProductResponse.CategoryInfo.builder()
                .categoryId(category.getId())
                .categoryName(category.getName())
                .parentCategoryId(parentId)
                .parentCategoryName(parentName)
                .build();
    }

    public List<GetProductResponse> getMainPageProducts() {
        // TODO: 일단은 상태가 '판매중'인 신상품 4개 추천
        List<Product> products = productRepository.findTop8ByProductStateOrderByIdDesc(SELLING);
        List<ProductImage> productImages = productImageRepository.findByProductInAndIsMainImage(products, true);

        return products.stream()
                .map(product -> GetProductResponse.builder()
                        .product(product)
                        .mainProductImage(productImages.stream()
                                .filter(productImage -> productImage.getProduct().getId().equals(product.getId()))
                                .findFirst()
                                .orElseThrow(() -> new CustomException(PRODUCT_MAIN_IMAGE_NOT_FOUND)))
                        .build())
                .collect(Collectors.toList());
    }
      
    /**
     * <h1>상품 상세 조회</h1>
     * @param productId 상품 아이디
     * @return RegisterProductResponse 상품 상세 정보
     * @author seochanhyeok
     */
    public RegisterProductResponse getProductDetail(Long productId) {

        Product product = productRepository.findById(productId).orElseThrow(() -> new CustomException(PRODUCT_NOT_FOUND));

        List<ProductImage> productImages = productImageRepository.findAllByProductId(productId);

        // 카테고리 리스트 조회
        List<Long> categoryIds = productCategoryRepository.findAllByProductId(productId)
                .stream()
                .map(ProductCategory::getCategory)
                .map(Category::getId)
                .collect(Collectors.toList());

        List<RegisterProductResponse.CategoryInfo> categoryInfoList = categoryRepository.findAllById(categoryIds)
                .stream()
                .map(this::mapCategoryToCategoryInfo)
                .collect(Collectors.toList());

        return RegisterProductResponse.builder()
                .product(product)
                .mainProductImage(productImages.stream()
                        .filter(ProductImage::getIsMainImage)
                        .findFirst()
                        .orElseThrow(() -> new CustomException(PRODUCT_IMAGE_NOT_FOUND)))
                .detailProductImages(productImages.stream()
                        .filter(productImage -> !productImage.getIsMainImage())
                        .collect(Collectors.toList()))
                .categoryInfoList(categoryInfoList)
                .build();
    }

    /**
     * 최신순으로 전체 또는 특정 카테고리의 상품 조회
     * No Offset Pagination (페이징 성능 향상)
     */
    public List<GetProductResponse> getPageByProductRegistrationDate(Long cursorId, Long categoryId, int pageSize) {
        return productRepository.findPageByProductRegistrationDate(cursorId, categoryId, pageSize);
    }

    /**
     * 가격높은순으로 전체 또는 특정 카테고리의 상품 조회
     * No Offset Pagination (페이징 성능 향상)
     */
    public List<GetProductResponse> getPageByProductPriceDesc(Long cursorId, Long cursorPrice, Long categoryId, int pageSize) {
        return productRepository.findPageByProductPriceDesc(cursorId, cursorPrice, categoryId, pageSize);
    }

    /**
     * 가격낮은순으로 전체 또는 특정 카테고리의 상품 조회
     * No Offset Pagination (페이징 성능 향상)
     */
    public List<GetProductResponse> getPageByProductPriceAsc(Long cursorId, Long cursorPrice, Long categoryId, int pageSize) {
        return productRepository.findPageByProductPriceAsc(cursorId, cursorPrice, categoryId, pageSize);
    }

    /**
     * 키워드로 상품 검색 (최신순)
     * 제품명 또는 브랜드명
     * No Offset Pagination (페이징 성능 향상)
     */
    public List<GetProductResponse> getPageByKeyword(String keyword, Long cursorId, int pageSize) {
        return productRepository.getSearchProducts(keyword, cursorId, pageSize);
    }

    /**
     * 키워드로 상품 검색 (가격높은순 / 가격낮은순)
     * 제품명 또는 브랜드명
     * No Offset Pagination (페이징 성능 향상)
     */
    public List<GetProductResponse> getPageByKeywordSortByPrice(String keyword, Long cursorId, Long cursorPrice, int pageSize, String sortType) {
        return productRepository.getSearchProductsByPrice(keyword, cursorId, cursorPrice, pageSize, sortType);
    }

    public Boolean submitPrice(Long productId, Long price, String token) {

        Member member = jwtProvider.getMemberByRawToken(token);

        Product product = productRepository.findById(productId).orElseThrow(() -> new CustomException(PRODUCT_NOT_FOUND));

        productValidator.validateProductByMemberId(product, member.getId());

        product.changePrice(price);

        product.changeProductState(SELLING);

        return true;

    }
}
