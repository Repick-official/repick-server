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

    public List<RegisterProductResponse> getMainDummyProducts() {
        Product product1 = productRepository.findById(11L).orElseThrow(() -> new CustomException(INTERNAL_SERVER_ERROR));
        Product product2 = productRepository.findById(12L).orElseThrow(() -> new CustomException(INTERNAL_SERVER_ERROR));
        Product product3 = productRepository.findById(13L).orElseThrow(() -> new CustomException(INTERNAL_SERVER_ERROR));
        Product product4 = productRepository.findById(14L).orElseThrow(() -> new CustomException(INTERNAL_SERVER_ERROR));

        ProductImage main1 = productImageRepository.findById(23L).orElseThrow();
        ProductImage main2 = productImageRepository.findById(27L).orElseThrow();
        ProductImage main3 = productImageRepository.findById(30L).orElseThrow();
        ProductImage main4 = productImageRepository.findById(34L).orElseThrow();


        List<RegisterProductResponse> registerProductResponses = new ArrayList<>();
        registerProductResponses.add(RegisterProductResponse.builder()
                .product(product1)
                .mainProductImage(main1)
                .build());
        registerProductResponses.add(RegisterProductResponse.builder()
                .product(product2)
                .mainProductImage(main2)
                .build());
        registerProductResponses.add(RegisterProductResponse.builder()
                .product(product3)
                .mainProductImage(main3)
                .build());
        registerProductResponses.add(RegisterProductResponse.builder()
                .product(product4)
                .mainProductImage(main4)
                .build());
        return registerProductResponses;
    }
}
