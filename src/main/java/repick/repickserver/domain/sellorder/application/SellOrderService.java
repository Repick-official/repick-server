package repick.repickserver.domain.sellorder.application;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import repick.repickserver.domain.member.domain.Member;
import repick.repickserver.domain.model.Bank;
import repick.repickserver.domain.ordernumber.application.OrderNumberService;
import repick.repickserver.domain.ordernumber.domain.OrderType;
import repick.repickserver.domain.product.dao.ProductImageRepository;
import repick.repickserver.domain.product.dao.ProductRepository;
import repick.repickserver.domain.product.domain.Product;
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
import repick.repickserver.infra.sms.service.SmsService;

import javax.transaction.Transactional;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import static repick.repickserver.domain.product.domain.ProductState.SETTLEMENT_COMPLETED;
import static repick.repickserver.domain.product.domain.ProductState.SETTLEMENT_REQUESTED;
import static repick.repickserver.domain.sellorder.domain.SellState.CANCELLED;
import static repick.repickserver.domain.sellorder.domain.SellState.REQUESTED;
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
    private final SmsService smsService;

    public SellOrderResponse postSellOrder(SellOrderRequest request, String token) {

        Member member = jwtProvider.getMemberByRawToken(token);

        sellOrderValidator.validateSellOrder(request);

        String orderNumber = orderNumberService.generateOrderNumber(OrderType.SELL_ORDER);

        SellOrder sellOrder = SellOrder.of(request, orderNumber, member);

        sellOrderRepository.save(sellOrder);

        sellOrderStateRepository.save(SellOrderState.of(sellOrder, REQUESTED));

        smsService.sendSellOrderSms(sellOrder.getPhoneNumber());

        slackNotifier.sendSellOrderSlackNotification(slackMapper.toSellOrderSlackNoticeString(request, orderNumber));

        // 처음 옷장 정리를 신청한 사용자의 경우
        if (!sellOrderRepository.existsByMemberId(member.getId()))
            smsService.BagPendingSender(sellOrder.getName(), sellOrder.getPhoneNumber());

        return SellOrderResponse.from(sellOrder);
    }

    public List<SellOrderResponse> getAllSellOrders(String token) {
        Member member = jwtProvider.getMemberByRawToken(token);
        return sellOrderRepository.getSellOrderResponseById(member.getId());
    }

    public List<SellOrderResponse> getSellOrders(String state, String token) {
        Member member = jwtProvider.getMemberByRawToken(token);

        SellState reqState = Parser.parseSellState(state);

        return sellOrderRepository.getSellOrdersByMemberIdAndState(member.getId(), reqState);
    }

    public List<SellOrderResponse> getAllSellOrdersAdmin(String state) {
        SellState reqState = Parser.parseSellState(state);

        return sellOrderRepository.getSellOrdersByState(reqState);
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

        // 리픽백 배출 완료시 슬랙 알림
        if (request.getSellState() == SellState.BAG_READY)
            slackNotifier.sendSellOrderSlackNotification(slackMapper.toSellOrderBagReadySlackNoticeString(sellOrder));

        return SellOrderResponse.from(sellOrder);
    }

    public List<GetProductResponse> getProductByProductState(String token, ProductState state1, ProductState state2) {
        Member member = jwtProvider.getMemberByRawToken(token);

        return productRepository.getProductResponseByMemberIdAndState(member.getId(), state1, state2);

    }

    public List<GetProductResponse> getProductByProductState(String token, ProductState state) {
        Member member = jwtProvider.getMemberByRawToken(token);

        return productRepository.getProductResponseByMemberIdAndState(member.getId(), state);

    }

    public List<GetProductResponse> getAllProductByMember(String token) {
        Member member = jwtProvider.getMemberByRawToken(token);

        return productRepository.findByMemberId(member.getId());
    }

    public Boolean requestSettlement(String token, SettlementRequest settlementRequest) {

        Member member = jwtProvider.getMemberByRawToken(token);

        settlementRequestValidator.validateSettlementRequest(settlementRequest);

        List<Product> productList = productRepository.findAllByIdListAndMember(settlementRequest.getProductIds(), member);

        productValidator.validateProductListByProductIds(productList, settlementRequest.getProductIds());

        Long totalPrice = calculateTotalPriceAndUpdateProductState(productList);

        if (settlementRequest.getBank() != null) sendSlackNotifier(productList, totalPrice, settlementRequest.getBank());
        else sendSlackNotifier(productList, totalPrice, member);

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

    private void sendSlackNotifier(List<Product> productList, Long totalPrice, Member member) {
        StringBuilder sb = new StringBuilder(); // 슬랙 메세지를 위한 StringBuilder
        sb.append("정산 신청입니다.\n").append("신청 상품 정보들: \n");

        AtomicReference<Bank> bankRef = new AtomicReference<>(null);

        productList.forEach(product -> {
            // 슬랙 메세지에 상품 정보 추가
            sb.append(product.getProductNumber()).append(" ").append(product.getPrice()).append("원\n");
            if (product.getSellOrder().getBank() != null) bankRef.set(product.getSellOrder().getBank());

        });

        Bank bank = bankRef.get(); // SellOrder의 계좌 정보를 우선으로 가져옴
        if (bank == null) bank = member.getBank(); // SellOrder의 계좌 정보가 없으면 Member의 계좌 정보를 가져옴
        if (bank == null) throw new CustomException(BANK_INFO_NOT_FOUND);

        sb.append("총 가격: ").append(totalPrice).append("원\n");
        sb.append(bank.getAccountNumber()).append(" ").append(bank.getBankName()).append("\n");
        slackNotifier.sendExpenseSettlementSlackNotification(sb.toString());
    }

    private void sendSlackNotifier(List<Product> productList, Long totalPrice, Bank bank) {
        StringBuilder sb = new StringBuilder(); // 슬랙 메세지를 위한 StringBuilder
        sb.append("정산 신청입니다.\n").append("신청 상품 정보들: \n");

        productList.forEach(product -> {
            // 슬랙 메세지에 상품 정보 추가
            sb.append(product.getProductNumber()).append(" ").append(product.getPrice()).append("원\n");
        });

        sb.append("총 가격: ").append(totalPrice).append("원\n");
        sb.append(bank.getAccountNumber()).append(" ").append(bank.getBankName()).append("\n");
        slackNotifier.sendExpenseSettlementSlackNotification(sb.toString());
    }

    public Boolean updateSettlementState(UpdateSettlementStateRequest request) {
        Product product = productRepository.findByProductNumber(request.getProductNumber())
                .orElseThrow(() -> new CustomException(PRODUCT_NOT_FOUND));

        productValidator.validateProductIsSettlementRequested(product);

        product.changeProductState(SETTLEMENT_COMPLETED);

        return true;
    }

    public Long getBagRequestCount() {
        return sellOrderRepository.countBySellState(REQUESTED);
    }

    public Long getSettlementRequestCount() {
        return productRepository.countByProductState(SETTLEMENT_REQUESTED);
    }

    public Boolean updateSellOrderStateToBagReady(String token, SellOrderUpdateRequest request) {

        Member member = jwtProvider.getMemberByRawToken(token);

        SellOrder sellOrder = findByOrderNumber(request.getOrderNumber());
        sellOrderValidator.validateIsSellOrderStateBagPending(sellOrder);
        sellOrderValidator.validateSellOrderMatchesMemberId(sellOrder, member.getId());

        sellOrderStateRepository.save(SellOrderState.of(sellOrder, SellState.BAG_READY));
        return true;
    }

    public Boolean cancelSellOrder(String token, SellOrderUpdateRequest request) {

        Member member = jwtProvider.getMemberByRawToken(token);

        SellOrder sellOrder = findByOrderNumber(request.getOrderNumber());
        sellOrderValidator.validateIsSellOrderStateRequested(sellOrder);
        sellOrderValidator.validateSellOrderMatchesMemberId(sellOrder, member.getId());
        
        sellOrderStateRepository.save(SellOrderState.of(sellOrder, CANCELLED));
        return true;

    }
}