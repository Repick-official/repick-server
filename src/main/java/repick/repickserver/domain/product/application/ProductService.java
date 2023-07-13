package repick.repickserver.domain.product.application;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import repick.repickserver.domain.product.dao.ProductImageRepository;
import repick.repickserver.domain.product.dao.ProductRepository;
import repick.repickserver.domain.product.domain.*;
import repick.repickserver.domain.product.dto.RegisterProductRequest;
import repick.repickserver.domain.product.dto.RegisterProductResponse;
import repick.repickserver.global.error.exception.CustomException;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static repick.repickserver.global.error.exception.ErrorCode.*;

@Service
@Transactional
@RequiredArgsConstructor
public class ProductService {

    private final AwsS3Service awsS3Service;
    private final ProductRepository productRepository;
    private final ProductImageRepository productImageRepository;

    public RegisterProductResponse registerProduct(MultipartFile mainImageFile, List<MultipartFile> detailImageFiles,
                                                   RegisterProductRequest request) {
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

        // 상품 정보 저장
        Product product = Product.builder()
                .name(request.getName())
                .detail(request.getDetail())
                .price(request.getPrice())
                .size(request.getSize())
                .discountRate(request.getDiscountRate())
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

        return RegisterProductResponse.builder()
                .product(savedProduct)
                .mainProductImage(savedMainProductImage)
                .detailProductImages(savedDetailProductImages)
                .build();
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

        return RegisterProductResponse.builder()
                .product(product)
                .mainProductImage(productImages.stream()
                        .filter(ProductImage::getIsMainImage)
                        .findFirst()
                        .orElseThrow(() -> new CustomException(PRODUCT_IMAGE_NOT_FOUND)))
                .detailProductImages(productImages.stream()
                        .filter(productImage -> !productImage.getIsMainImage())
                        .collect(Collectors.toList()))
                .build();
    }


    // TODO : 삭제..
    public List<RegisterProductResponse> getMainDummyProducts() {
        // ㅋㅋ..뭐요!!!
        Product product1 = productRepository.findById(11L).orElseThrow(() -> new CustomException(INTERNAL_SERVER_ERROR));
        Product product2 = productRepository.findById(12L).orElseThrow(() -> new CustomException(INTERNAL_SERVER_ERROR));
        Product product3 = productRepository.findById(13L).orElseThrow(() -> new CustomException(INTERNAL_SERVER_ERROR));
        Product product4 = productRepository.findById(14L).orElseThrow(() -> new CustomException(INTERNAL_SERVER_ERROR));

        ProductImage main1 = productImageRepository.findById(23L).orElseThrow();
        ProductImage main2 = productImageRepository.findById(27L).orElseThrow();
        ProductImage main3 = productImageRepository.findById(30L).orElseThrow();
        ProductImage main4 = productImageRepository.findById(34L).orElseThrow();

        ProductImage detail1_1 = productImageRepository.findById(24L).orElseThrow();
        ProductImage detail1_2 = productImageRepository.findById(25L).orElseThrow();
        ProductImage detail1_3 = productImageRepository.findById(26L).orElseThrow();
        ProductImage detail2_1 = productImageRepository.findById(28L).orElseThrow();
        ProductImage detail2_2 = productImageRepository.findById(29L).orElseThrow();
        ProductImage detail3_1 = productImageRepository.findById(31L).orElseThrow();
        ProductImage detail3_2 = productImageRepository.findById(32L).orElseThrow();
        ProductImage detail3_3 = productImageRepository.findById(33L).orElseThrow();
        ProductImage detail4_1 = productImageRepository.findById(35L).orElseThrow();
        ProductImage detail4_2 = productImageRepository.findById(36L).orElseThrow();
        ProductImage detail4_3 = productImageRepository.findById(37L).orElseThrow();
        ProductImage detail4_4 = productImageRepository.findById(38L).orElseThrow();

        List<RegisterProductResponse> registerProductResponses = new ArrayList<>();

        registerProductResponses.add(RegisterProductResponse.builder()
                .product(product1)
                .mainProductImage(main1)
                .detailProductImages(List.of(detail1_1, detail1_2, detail1_3))
                .build());

        registerProductResponses.add(RegisterProductResponse.builder()
                .product(product2)
                .mainProductImage(main2)
                .detailProductImages(List.of(detail2_1, detail2_2))
                .build());
        registerProductResponses.add(RegisterProductResponse.builder()
                .product(product3)
                .mainProductImage(main3)
                .detailProductImages(List.of(detail3_1, detail3_2, detail3_3))
                .build());
        registerProductResponses.add(RegisterProductResponse.builder()
                .product(product4)
                .mainProductImage(main4)
                .detailProductImages(List.of(detail4_1, detail4_2, detail4_3, detail4_4))
                .build());
        return registerProductResponses;
    }
}
