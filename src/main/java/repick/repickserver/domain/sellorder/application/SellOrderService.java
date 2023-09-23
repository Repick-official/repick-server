package repick.repickserver.domain.sellorder.application;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import repick.repickserver.domain.member.domain.Member;
import repick.repickserver.domain.ordernumber.application.OrderNumberService;
import repick.repickserver.domain.ordernumber.domain.OrderType;
import repick.repickserver.domain.product.dao.ProductImageRepository;
import repick.repickserver.domain.product.dao.ProductRepository;
import repick.repickserver.domain.product.domain.Product;
import repick.repickserver.domain.product.domain.ProductImage;
import repick.repickserver.domain.product.domain.ProductState;
import repick.repickserver.domain.product.dto.GetProductResponse;
import repick.repickserver.domain.product.validator.ProductValidator;
import repick.repickserver.domain.sellorder.domain.SellOrder;
import repick.repickserver.domain.sellorder.domain.SellOrderState;
import repick.repickserver.domain.sellorder.domain.SellState;
import repick.repickserver.domain.sellorder.dto.*;
import repick.repickserver.domain.sellorder.repository.SellOrderRepository;
import repick.repickserver.domain.sellorder.repository.SellOrderStateRepository;
import repick.repickserver.domain.sellorder.validator.SellOrderValidator;
import repick.repickserver.domain.sellorder.validator.SettlementRequestValidator;
import repick.repickserver.global.Parser;
import repick.repickserver.global.error.exception.CustomException;
import repick.repickserver.global.jwt.JwtProvider;
import repick.repickserver.infra.slack.application.SlackNotifier;
import repick.repickserver.infra.slack.mapper.SlackMapper;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static repick.repickserver.domain.product.domain.ProductState.SETTLEMENT_COMPLETED;
import static repick.repickserver.domain.product.domain.ProductState.SETTLEMENT_REQUESTED;
import static repick.repickserver.global.error.exception.ErrorCode.*;

@Service
@Transactional
@RequiredArgsConstructor
public class SellOrderService {

    private final SellOrderRepository sellOrderRepository;
    private final SellOrderStateRepository sellOrderStateRepository;
    private final JwtProvider jwtProvider;
    private final OrderNumberService orderNumberService;
    private final SlackNotifier slackNotifier;
    private final ProductRepository productRepository;
    private final ProductImageRepository productImageRepository;
    private final SellOrderValidator sellOrderValidator;
    private final SlackMapper slackMapper;
    private final SettlementRequestValidator settlementRequestValidator;
    private final ProductValidator productValidator;

    public SellOrderResponse postSellOrder(SellOrderRequest request, String token) {

        Member member = jwtProvider.getMemberByRawToken(token);

        sellOrderValidator.validateSellOrder(request);

        String orderNumber = orderNumberService.generateOrderNumber(OrderType.SELL_ORDER);

        String returnDate = Parser.normalizeReturnDateWithHyphen(request.getReturnDate());

        SellOrder sellOrder = SellOrder.of(request, orderNumber, member, returnDate);

        sellOrderRepository.save(sellOrder);

        sellOrderStateRepository.save(SellOrderState.of(sellOrder, SellState.REQUESTED));

        slackNotifier.sendSellOrderSlackNotification(slackMapper.toSellOrderSlackNoticeString(request, orderNumber));

        return SellOrderResponse.from(sellOrder);
    }

    public List<SellOrderResponse> getAllSellOrders(String token) {
        // 모두 가져오고 Response로 변환 후 반환
        return sellOrderRepository.getSellOrdersById(jwtProvider.getMemberByRawToken(token).getId()).stream()
                .map(SellOrderResponse::from)
                .collect(Collectors.toList());
    }

    public List<SellOrderResponse> getSellOrders(String state, String token) {
        // state 파싱
        SellState reqState = Parser.parseSellState(state);

        return sellOrderRepository.getSellOrdersByMemberIdAndState(jwtProvider.getMemberByRawToken(token).getId(), reqState).stream()
                .filter(sellOrder -> sellOrderStateRepository.isLastBySellOrderId(sellOrder.getId(), reqState))
                .map(SellOrderResponse::from)
                .collect(Collectors.toList());
    }

    public List<SellOrderResponse> getAllSellOrdersAdmin(String state) {
        SellState reqState = Parser.parseSellState(state);

        return sellOrderRepository.getSellOrdersByState(reqState).stream()
                .filter(sellOrder -> sellOrderStateRepository.isLastBySellOrderId(sellOrder.getId(), reqState))
                .map(SellOrderResponse::from)
                .collect(Collectors.toList());
    }

    private SellOrder findByOrderNumber(String orderNumber) {
        return sellOrderRepository.findByOrderNumber(orderNumber)
                .orElseThrow(() -> new CustomException(ORDER_NOT_FOUND));
    }

    /** ADMIN
     * 판매 주문 상태 업데이트
     */
    public SellOrderResponse updateSellOrderState(SellOrderUpdateRequest request) {
        SellOrder sellOrder = findByOrderNumber(request.getOrderNumber());
        sellOrderStateRepository.save(SellOrderState.of(sellOrder, request.getSellState()));
        return SellOrderResponse.from(sellOrder);
    }

    private List<GetProductResponse> handleProductList(List<Product> productList) {
        List<GetProductResponse> getProductResponses = new ArrayList<>();

        productList.forEach(product -> {

            // MainImage 찾기
            ProductImage productMainImage = productImageRepository.findByProductAndIsMainImage(product, true)
                    .orElseThrow(() -> new CustomException(PRODUCT_IMAGE_NOT_FOUND));

            getProductResponses.add(
                    GetProductResponse.builder()
                            .product(product)
                            .mainProductImage(productMainImage)
                            .build()
            );
        });

        return getProductResponses;
    }

    public List<GetProductResponse> getPublishedProduct(String token) {
        Member member = jwtProvider.getMemberByRawToken(token);
        List<Product> productList = productRepository.findByMemberId(member.getId());

        return handleProductList(productList);
    }

    public List<GetProductResponse> getPreparingProduct(String token) {
        Member member = jwtProvider.getMemberByRawToken(token);
        List<Product> productList = productRepository.findByMemberIdAndTwoStates(member.getId(), ProductState.PREPARING, ProductState.BEFORE_SMS);

        return handleProductList(productList);
    }

    public List<GetProductResponse> getSellingProduct(String token) {
        Member member = jwtProvider.getMemberByRawToken(token);
        List<Product> productList = productRepository.findByMemberIdAndTwoStates(member.getId(), ProductState.SELLING, ProductState.PENDING);

        return handleProductList(productList);
    }

    public List<GetProductResponse> getSoldProduct(String token) {
        Member member = jwtProvider.getMemberByRawToken(token);
        List<Product> productList = productRepository.findByMemberIdAndState(member.getId(), ProductState.SOLD_OUT);

        return handleProductList(productList);
    }

    public List<GetProductResponse> getSettledProduct(String token) {
        Member member = jwtProvider.getMemberByRawToken(token);
        List<Product> productList = productRepository.findByMemberIdAndTwoStates(member.getId(), SETTLEMENT_REQUESTED, SETTLEMENT_COMPLETED);

        return handleProductList(productList);
    }

    public Boolean requestSettlement(String token, SettlementRequest settlementRequest) {

        Member member = jwtProvider.getMemberByRawToken(token);

        settlementRequestValidator.validateSettlementRequest(settlementRequest);

        List<Product> productList = productRepository.findAllByIdListAndMember(settlementRequest.getProductIds(), member);

        productValidator.validateProductListByProductIds(productList, settlementRequest.getProductIds());

        Long totalPrice = calculateTotalPriceAndUpdateProductState(productList);

        sendSlackNotifier(productList, totalPrice);

        return true;

    }

    private Long calculateTotalPriceAndUpdateProductState(List<Product> productList) {
        Long totalPrice = 0L;

        for (Product product : productList) {
            settlementRequestValidator.validateSettlementRequestIsSoldOut(product);
            product.changeProductState(SETTLEMENT_REQUESTED);
            totalPrice += product.getPrice();
        }

        return totalPrice;
    }

    private void sendSlackNotifier(List<Product> productList, Long totalPrice) {
        StringBuilder sb = new StringBuilder(); // 슬랙 메세지를 위한 StringBuilder
        sb.append("정산 신청입니다.\n").append("신청 상품 정보들: \n");

        productList.forEach(product -> {
            // 슬랙 메세지에 상품 정보 추가
            sb.append(product.getProductNumber()).append(" ").append(product.getPrice()).append("원\n");
            sb.append(product.getSellOrder().getBank().getAccountNumber()).append(" ")
                    .append(product.getSellOrder().getBank().getBankName()).append("\n");
        });

        sb.append("총 가격: ").append(totalPrice).append("원\n");
        slackNotifier.sendExpenseSettlementSlackNotification(sb.toString());
    }

    public Boolean updateSettlementState(UpdateSettlementStateRequest request) {
        Product product = productRepository.findByProductNumber(request.getProductNumber())
                .orElseThrow(() -> new CustomException(PRODUCT_NOT_FOUND));

        productValidator.validateProductIsSettlementRequested(product);

        product.changeProductState(SETTLEMENT_COMPLETED);

        return true;
    }
}