package repick.repickserver.domain.sellorder.application;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import repick.repickserver.domain.member.domain.Member;
import repick.repickserver.domain.sellorder.dao.SellOrderRepository;
import repick.repickserver.domain.sellorder.dao.SellOrderStateRepository;
import repick.repickserver.domain.sellorder.domain.SellOrder;
import repick.repickserver.domain.sellorder.domain.SellOrderState;
import repick.repickserver.domain.sellorder.domain.SellState;
import repick.repickserver.domain.sellorder.dto.*;
import repick.repickserver.domain.sellorder.mapper.SellOrderMapper;
import repick.repickserver.domain.sellorder.validator.SellOrderValidator;
import repick.repickserver.domain.ordernumber.application.OrderNumberService;
import repick.repickserver.domain.ordernumber.domain.OrderType;
import repick.repickserver.domain.product.dao.ProductImageRepository;
import repick.repickserver.domain.product.dao.ProductRepository;
import repick.repickserver.domain.product.domain.Product;
import repick.repickserver.domain.product.domain.ProductImage;
import repick.repickserver.domain.product.domain.ProductState;
import repick.repickserver.domain.product.dto.GetProductResponse;
import repick.repickserver.global.Parser;
import repick.repickserver.global.error.exception.CustomException;
import repick.repickserver.global.jwt.JwtProvider;
import repick.repickserver.infra.slack.application.SlackNotifier;
import repick.repickserver.infra.slack.mapper.SlackMapper;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

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
    private final SellOrderMapper sellOrderMapper;
    private final SellOrderValidator sellOrderValidator;
    private final SlackMapper slackMapper;

    public SellOrderResponse postSellOrder(SellOrderRequest request, String token) {
        Member member = jwtProvider.getMemberByRawToken(token);

        sellOrderValidator.validateSellOrder(request);

        // 주문번호 생성
        String orderNumber = orderNumberService.generateOrderNumber(OrderType.SELL_ORDER);

        // sellOrder 생성
        SellOrder sellOrder = sellOrderMapper.toSellOrder(request, orderNumber, member);

        sellOrderRepository.save(sellOrder);

        // sellOrderState 생성
        sellOrderStateRepository.save(SellOrderState.builder()
                .sellOrder(sellOrder)
                .sellState(SellState.REQUESTED)
                .build());

        slackNotifier.sendSellOrderSlackNotification(slackMapper.toSellOrderSlackNoticeString(request, orderNumber));

        return sellOrderMapper.toSellOrderResponse(sellOrder);
    }

    public List<SellOrderResponse> getAllSellOrders(String token) {
        return sellOrderRepository.getSellOrdersById(jwtProvider.getMemberByRawToken(token).getId()).stream()
                .map(sellOrderMapper::toSellOrderResponse)
                .collect(Collectors.toList());
    }

    public List<SellOrderResponse> getSellOrders(String state, String token) {
        SellState reqState = Parser.parseSellState(state);

        return sellOrderRepository.getSellOrdersByIdAndState(jwtProvider.getMemberByRawToken(token).getId(), reqState).stream()
                .filter(sellOrder -> sellOrderStateRepository.isLastBySellOrderId(sellOrder.getId(), reqState))
                .map(sellOrderMapper::toSellOrderResponse)
                .collect(Collectors.toList());
    }

    public List<SellOrderResponse> getAllSellOrdersAdmin(String state) {
        SellState reqState = Parser.parseSellState(state);

        return sellOrderRepository.getSellOrdersByState(reqState).stream()
                .filter(sellOrder -> sellOrderStateRepository.isLastBySellOrderId(sellOrder.getId(), reqState))
                .map(sellOrderMapper::toSellOrderResponse)
                .collect(Collectors.toList());
    }

    public SellOrderResponse updateSellOrderState(SellOrderUpdateRequest request) {

        SellOrder sellOrder = sellOrderRepository.findByOrderNumber(request.getOrderNumber())
                .orElseThrow(() -> new CustomException(ORDER_NOT_FOUND));

        sellOrderStateRepository.save(SellOrderState.builder()
                .sellOrder(sellOrder)
                .sellState(request.getSellState())
                .build());

        return sellOrderMapper.toSellOrderResponse(sellOrder);
    }

    private List<GetProductResponse> handleProductList(List<Product> productList) {
        List<GetProductResponse> getProductResponses = new ArrayList<>();

        productList.forEach(product -> {

            // MainImage 찾기
            Optional<ProductImage> productMainImage = productImageRepository.findByProductAndIsMainImage(product, true);
            // MainImage 없으면 에러
            if (productMainImage.isEmpty()) throw new CustomException(PRODUCT_IMAGE_NOT_FOUND);

            getProductResponses.add(
                    GetProductResponse.builder()
                            .product(product)
                            .mainProductImage(productMainImage.get())
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
        List<Product> productList = productRepository.findByMemberIdAndTwoStates(member.getId(), ProductState.SETTLEMENT_REQUESTED, ProductState.SETTLEMENT_COMPLETED);

        return handleProductList(productList);
    }

    public Boolean requestSettlement(String token, SettlementRequest settlementRequest) {
        // productIds가 없을 경우 에러
        if (settlementRequest.getProductIds() == null) throw new CustomException(INVALID_REQUEST_ERROR);
        if (settlementRequest.getProductIds().size() == 0) throw new CustomException(INVALID_REQUEST_ERROR);

        Member member = jwtProvider.getMemberByRawToken(token);
        AtomicLong totalPrice = new AtomicLong(0); // 총 가격을 저장하기 위한 AtomicLong
        StringBuilder sb = new StringBuilder(); // 슬랙 메세지를 위한 StringBuilder
        sb.append("정산 신청입니다.\n").append("신청 상품 정보들: \n");

        settlementRequest.getProductIds().forEach(productId -> {
            // 신청한 멤버와 request의 멤버가 맞는지 검사
            Optional<Product> product = productRepository.findById(productId);
            if (product.isEmpty()) throw new CustomException(PRODUCT_NOT_FOUND); // 상품이 없을 경우 에러
            if (!product.get().getSellOrder().getMember().getId().equals(member.getId()))
                throw new CustomException(ORDER_MEMBER_NOT_MATCH); // 신청한 멤버와 request의 멤버가 맞지 않을 경우 에러
            if (product.get().getProductState() != ProductState.SOLD_OUT)
                throw new CustomException(PRODUCT_NOT_SOLD_OUT); // 판매중인 상품이 아닐 경우 에러

            // 정산 신청 상태로 변경
            product.get().changeProductState(ProductState.SETTLEMENT_REQUESTED);
            // 가격을 가져와 총 가격에 추가
            totalPrice.addAndGet(product.get().getPrice());
            // 슬랙 메세지에 상품 정보 추가
            sb.append(product.get().getProductNumber()).append(" ").append(product.get().getPrice()).append("원\n");
            sb.append(product.get().getSellOrder().getBank().getAccountNumber()).append(" ")
                    .append(product.get().getSellOrder().getBank().getBankName()).append("\n");
        });

        sb.append("총 가격: ").append(totalPrice).append("원\n");
        slackNotifier.sendExpenseSettlementSlackNotification(sb.toString());
        return true;

    }

    public Boolean updateSettlementState(UpdateSettlementStateRequest request) {
        Optional<Product> product = productRepository.findByProductNumber(request.getProductNumber());
        if (product.isEmpty()) throw new CustomException(PRODUCT_NOT_FOUND); // 상품이 없을 경우 에러

        if (product.get().getProductState() != ProductState.SETTLEMENT_REQUESTED)
            throw new CustomException(PRODUCT_NOT_SETTLEMENT_REQUESTED); // 정산 신청 상태가 아닐 경우 에러

        product.ifPresent(target ->
                target.changeProductState(ProductState.SETTLEMENT_COMPLETED)); // 정산 완료로 변경

        return true;
    }
}