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
import repick.repickserver.domain.cart.dto.HomeFittingRequest;
import repick.repickserver.domain.cart.dto.HomeFittingResponse;
import repick.repickserver.domain.member.application.SubscriberInfoService;
import repick.repickserver.domain.member.domain.Member;
import repick.repickserver.domain.member.validator.MemberValidator;
import repick.repickserver.domain.ordernumber.application.OrderNumberService;
import repick.repickserver.domain.ordernumber.domain.OrderType;
import repick.repickserver.domain.product.dao.ProductImageRepository;
import repick.repickserver.domain.product.dao.ProductRepository;
import repick.repickserver.global.error.exception.CustomException;
import repick.repickserver.global.jwt.JwtProvider;
import repick.repickserver.infra.slack.application.SlackNotifier;

import java.util.List;
import java.util.stream.Collectors;

import static repick.repickserver.domain.cart.domain.CartProductState.HOME_FITTING_REQUESTED;
import static repick.repickserver.domain.cart.domain.CartProductState.IN_CART;
import static repick.repickserver.domain.product.domain.ProductState.PENDING;
import static repick.repickserver.domain.product.domain.ProductState.SELLING;
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
    private final OrderNumberService orderNumberService;
    private final SlackNotifier slackNotifier;
    private final MemberValidator memberValidator;

    public List<HomeFittingResponse> requestHomeFitting(HomeFittingRequest homeFittingRequest, String token) {

        // 토큰으로 멤버의 구독여부를 반환합니다. ( "NONE": 구독 안함, "BASIC": 베이직 플랜, "PRO": 프로 플랜 )
        // 구독 안한 회원인 경우, 홈피팅 신청 불가하므로 ACCESS_DENIED_NOT_SUBSCRIBED 에러를 던집니다.
        if (subscriberInfoService.check(token).equals("NONE"))
            throw new CustomException(ACCESS_DENIED_NOT_SUBSCRIBED);

        // 신청하는 회원이 기본 정보를 가지고 있는지 체크합니다.
        if (!memberValidator.check_info(jwtProvider.getMemberByRawToken(token)))
            throw new CustomException(ACCESS_DENIED_NO_USER_INFO);

        List<CartProduct> cartProducts = cartProductRepository.findAllById(homeFittingRequest.getCartProductIds());

        if (cartProducts.isEmpty()) {
            throw new CustomException(INVALID_CART_PRODUCT_ID);
        }

        for (CartProduct cartProduct : cartProducts) {
            // 판매중인 상품인지 확인
            // if(!productRepository.existsByIdAndProductState(cartProduct.getProduct().getId(), SELLING))
            //     throw new CustomException(PRODUCT_NOT_SELLING);

            // 판매중인 상품인지 확인 후 상태 변경
            productRepository.findByIdAndProductState(cartProduct.getProduct().getId(), SELLING)
                    .ifPresentOrElse(
                            product -> {
                                // 상품의 상태를 PENDING 상태로 변경
                                product.changeProductState(PENDING);
                            },
                            () -> {
                                throw new CustomException(PRODUCT_NOT_SELLING);
                            }
                    );

            // 마이픽에 담긴 상품인지 확인
            if (cartProduct.getCartProductState().equals(IN_CART)) {
                cartProduct.changeState(HOME_FITTING_REQUESTED);
            } else {
                throw new CustomException(INVALID_CART_PRODUCT_STATE);
            }

        }

        String orderNumber = orderNumberService.generateOrderNumber(OrderType.HOME_FITTING);

        // 슬랙 알림 메세지
        slackNotifier.sendHomeFittingSlackNotification(
                        "홈피팅 신청입니다.\n" +
                        "주문번호: " + orderNumber + "\n");

        return cartProducts.stream()
                .map(cartProduct -> {
                    HomeFitting homeFitting = HomeFitting.builder()
                            .cartProduct(cartProduct)
                            .orderNumber(orderNumber)
                            .build();

                    homeFitting = homeFittingRepository.save(homeFitting);

                    // 슬랙 알림 메세지
                    slackNotifier.sendHomeFittingSlackNotification(
                                    "상품명: " + cartProduct.getProduct().getName() + "\n" +
                                    "상품번호: " + cartProduct.getProduct().getProductNumber() + "\n");

                    return new HomeFittingResponse(homeFitting);
                })
                .collect(Collectors.toList());
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
