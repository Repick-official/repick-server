package repick.repickserver.domain.homefitting.validator;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import repick.repickserver.domain.homefitting.dao.HomeFittingRepository;
import repick.repickserver.domain.member.domain.Member;
import repick.repickserver.domain.product.dao.ProductRepository;
import repick.repickserver.domain.product.domain.Product;
import repick.repickserver.global.error.exception.CustomException;

import static repick.repickserver.domain.product.domain.ProductState.PENDING;
import static repick.repickserver.domain.product.domain.ProductState.SELLING;
import static repick.repickserver.global.error.exception.ErrorCode.PRODUCT_NOT_SELLING;

@Service @RequiredArgsConstructor
public class HomeFittingValidator {

    private final HomeFittingRepository homeFittingRepository;
    private final ProductRepository productRepository;

    public Boolean isHomeFittingProduct(Product product, Member member) {
        return homeFittingRepository.existsByMemberAndProduct(member.getId(), product.getId());
    }

    public Product validateSellingProduct(Member member, Long productId) {
        return productRepository.findByIdAndProductState(productId, SELLING)
                .orElseGet(() -> productRepository.findByIdAndProductState(productId, PENDING)
                        .filter(p -> isHomeFittingProduct(p, member))
                        .orElseThrow(() -> new CustomException(PRODUCT_NOT_SELLING)));
    }



}
