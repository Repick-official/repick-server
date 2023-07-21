package repick.repickserver.domain.cart.application;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import repick.repickserver.domain.cart.dao.CartProductRepository;
import repick.repickserver.domain.cart.dao.CartRepository;
import repick.repickserver.domain.cart.dao.HomeFittingRepository;
import repick.repickserver.domain.cart.domain.Cart;
import repick.repickserver.domain.cart.domain.CartProduct;
import repick.repickserver.domain.cart.domain.HomeFitting;
import repick.repickserver.domain.cart.dto.GetHomeFittingResponse;
import repick.repickserver.domain.cart.dto.HomeFittingResponse;
import repick.repickserver.domain.member.domain.Member;
import repick.repickserver.domain.product.dao.ProductImageRepository;
import repick.repickserver.domain.product.dao.ProductRepository;
import repick.repickserver.domain.product.domain.Product;
import repick.repickserver.global.error.exception.CustomException;
import repick.repickserver.global.jwt.JwtProvider;
import java.util.List;
import repick.repickserver.domain.member.application.SubscriberInfoService;
import repick.repickserver.infra.SlackNotifier;

import static repick.repickserver.domain.cart.domain.CartProductState.HOME_FITTING_REQUESTED;
import static repick.repickserver.domain.cart.domain.CartProductState.IN_CART;
import static repick.repickserver.domain.product.domain.ProductState.*;
import static repick.repickserver.global.error.exception.ErrorCode.*;

@Service
@Transactional
@RequiredArgsConstructor
public class HomeFittingService {

    private final HomeFittingRepository homeFittingRepository;
    private final CartProductRepository cartProductRepository;
    private final CartRepository cartRepository;
    private final ProductRepository productRepository;
    private final ProductImageRepository productImageRepository;
    private final JwtProvider jwtProvider;
    private final SubscriberInfoService subscriberInfoService;
    private final SlackNotifier slackNotifier;

    public HomeFittingResponse requestHomeFitting(Long cartProductId, String token) {

        // 토큰으로 멤버의 구독여부를 반환합니다. ( "NONE": 구독 안함, "BASIC": 베이직 플랜, "PRO": 프로 플랜 )
        String check = subscriberInfoService.check(token);

        // 구독 안한 회원인 경우, 홈피팅 신청 불가하므로 ACCESS_DENIED_NOT_SUBSCRIBED 에러를 던집니다.
        if (check.equals("NONE")) throw new CustomException(ACCESS_DENIED_NOT_SUBSCRIBED);

        CartProduct cartProduct = cartProductRepository.findById(cartProductId)
                .orElseThrow(() -> new CustomException(INVALID_CART_PRODUCT_ID));

        Product product = productRepository.findByIdAndProductState(cartProduct.getProduct().getId(), SELLING)
                .orElseThrow(() -> new CustomException(PRODUCT_NOT_SELLING));

        // 마이픽에 담긴 상품인지 확인
        if(cartProduct.getCartProductState().equals(IN_CART)) {
            cartProduct.changeState(HOME_FITTING_REQUESTED);
        }
        else {
            throw new CustomException(INVALID_CART_PRODUCT_STATE);
        }

        // Slack에 알림 보내기
        slackNotifier.sendHomeFittingSlackNotification("홈피팅 신청이 들어왔습니다.\n" +
                "신청자: " + cartProduct.getCart().getMember().getName() + "\n" +
                "상품명: " + product.getName() + "\n" +
                "주소: " + cartProduct.getCart().getMember().getAddress().getMainAddress() + "\n" +
                "상세주소: " + cartProduct.getCart().getMember().getAddress().getDetailAddress() + "\n" +
                "우편번호: " + cartProduct.getCart().getMember().getAddress().getZipCode() + "\n" +
                "연락처: " + cartProduct.getCart().getMember().getPhoneNumber() + "\n"
        );
        // FIXME: 2023/07/20 상품정보에 주소지가 없습니다.. 일단 회원정보로 대체합니다.


        return HomeFittingResponse.builder()
                .homeFitting(homeFittingRepository.save(
                        HomeFitting.builder()
                                .cartProduct(cartProduct)
                                .build()))
                .build();
    }

    public List<GetHomeFittingResponse> getMyHomeFitting(String token) {
        Member member = jwtProvider.getMemberByRawToken(token);
        Cart cart = cartRepository.findByMember(member);

        return productRepository.getHomeFittingProducts(cart.getId());
    }

    public List<GetHomeFittingResponse> getHomeFitting(String homeFittingState) {
        return productRepository.getAllHomeFittingProducts(homeFittingState);
    }

    public void changeHomeFittingState(Long homeFittingId, String homeFittingState) {
        HomeFitting homeFitting = homeFittingRepository.findById(homeFittingId)
                .orElseThrow(() -> new CustomException(INVALID_HOME_FITTING_ID));

        homeFitting.changeState(homeFittingState);
    }
}
