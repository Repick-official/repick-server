package repick.repickserver.domain.order.application;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import repick.repickserver.domain.member.domain.Member;
import repick.repickserver.domain.order.dao.SellOrderRepository;
import repick.repickserver.domain.order.dao.SellOrderStateRepository;
import repick.repickserver.domain.order.domain.SellOrder;
import repick.repickserver.domain.order.domain.SellOrderState;
import repick.repickserver.domain.order.domain.SellState;
import repick.repickserver.domain.order.dto.SellOrderRequest;
import repick.repickserver.domain.order.dto.SellOrderResponse;
import repick.repickserver.domain.order.dto.SellOrderUpdateRequest;
import repick.repickserver.domain.order.dto.SettlementRequest;
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

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

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

    /**
     * 판매 수거 요청
     * @param request (name, phoneNumber, bankName, accountNumber, bagQuantity, productQuantity, address, requestDetail, returnDate)
     * @param token (accessToken)
     * @return SellOrderResponse (id, name, orderNumber, phoneNumber, bankName, accountNumber, bagQuantity, productQuantity, address, requestDetail, returnDate, sellState)
     * @exception CustomException (ORDER_FAIL)
     * @author seochanhyeok
     */
    public SellOrderResponse postSellOrder(SellOrderRequest request, String token) {
        Member member = jwtProvider.getMemberByRawToken(token);

        try {
            // 주문번호 생성
            String orderNumber = orderNumberService.generateOrderNumber(OrderType.SELL_ORDER);

            // sellOrder 생성
            SellOrder sellOrder = SellOrder.builder()
                    .name(request.getName())
                    .orderNumber(orderNumber)
                    .phoneNumber(request.getPhoneNumber())
                    .bank(request.getBank())
                    .bagQuantity(request.getBagQuantity())
                    .productQuantity(request.getProductQuantity())
                    .address(request.getAddress())
                    .requestDetail(request.getRequestDetail())
                    .returnDate(request.getReturnDate())
                    .member(member)
                    .build();

            sellOrderRepository.save(sellOrder);

            // sellOrderState 생성
            sellOrderStateRepository.save(SellOrderState.builder()
                    .sellOrder(sellOrder)
                    .sellState(SellState.REQUESTED)
                    .build());

            slackNotifier.sendSellOrderSlackNotification("판매 수거 요청이 들어왔습니다.\n" +
                    "주문번호: " + orderNumber + "\n" +
                    "이름: " + request.getName() + "\n" +
                    "연락처: " + request.getPhoneNumber() + "\n" +
                    "주소: " + request.getAddress().mainAddress + "\n" +
                    "상세주소: " + request.getAddress().detailAddress + "\n" +
                    "우편번호: " + request.getAddress().zipCode + "\n" +
                    "수거 시 희망사항: " + request.getRequestDetail() + "\n" +
                    "수거 희망일: " + request.getReturnDate() + "\n" +
                    "의류 수량: " + request.getProductQuantity() + "\n" +
                    "리픽백 수량: " + request.getBagQuantity());

            return SellOrderResponse.builder()
                    .id(sellOrder.getId())
                    .name(sellOrder.getName())
                    .orderNumber(sellOrder.getOrderNumber())
                    .phoneNumber(sellOrder.getPhoneNumber())
                    .bank(sellOrder.getBank())
                    .bagQuantity(sellOrder.getBagQuantity())
                    .productQuantity(sellOrder.getProductQuantity())
                    .address(sellOrder.getAddress())
                    .requestDetail(sellOrder.getRequestDetail())
                    .returnDate(sellOrder.getReturnDate())
                    .sellState(SellState.REQUESTED)
                    .build();

        } catch (Exception e) {
            System.out.println(e.getMessage());
            throw new CustomException(ORDER_FAIL);
        }

    }

    /**
     * <h1>모든 판매 조회</h1>
     * @param token (accessToken)
     * @return List<SellOrderResponse> (name, phoneNumber, bankName, accountNumber, bagQuantity, productQuantity, address, requestDetail, returnDate, sellState)
     * @exception CustomException (PATH_NOT_RESOLVED)
     * @apiNote 사용자의 판매 주문들을 sellState 와 관계 없이 가져옵니다.
     * @author seochanhyeok
     */
    public List<SellOrderResponse> getAllSellOrders(String token) {
        Member member = jwtProvider.getMemberByRawToken(token);

        List<SellOrderResponse> sellOrderResponses = new ArrayList<>();

        sellOrderRepository.getSellOrdersById(member.getId()).forEach(sellOrder -> sellOrderResponses.add(
                SellOrderResponse.builder()
                        .id(sellOrder.getId())
                        .orderNumber(sellOrder.getOrderNumber())
                        .name(sellOrder.getName())
                        .phoneNumber(sellOrder.getPhoneNumber())
                        .bank(sellOrder.getBank())
                        .bagQuantity(sellOrder.getBagQuantity())
                        .productQuantity(sellOrder.getProductQuantity())
                        .address(sellOrder.getAddress())
                        .requestDetail(sellOrder.getRequestDetail())
                        .returnDate(sellOrder.getReturnDate())
                        // 가장 최근에 업데이트된 state 가져옴
                        .sellState(sellOrderStateRepository.findLastStateBySellOrderId(sellOrder.getId()).getSellState())
                        .createdDate(sellOrder.getCreatedDate())
                        .build()
        ));

        return sellOrderResponses;

    }

    /**
     * <h1>판매 조회</h1>
     * @param state (requested | canceled | delivered | published)
     * @param token (accessToken)
     * @return List<SellOrderResponse> (name, phoneNumber, bankName, accountNumber, bagQuantity, productQuantity, address, requestDetail, returnDate, sellState)
     * @exception CustomException (PATH_NOT_RESOLVED)
     * @author seochanhyeok
     */
    public List<SellOrderResponse> getSellOrders(String state, String token) {
        Member member = jwtProvider.getMemberByRawToken(token);

        SellState reqState = Parser.parseSellState(state);

        List<SellOrderResponse> sellOrderResponses = new ArrayList<>();
        sellOrderRepository.getSellOrdersByIdAndState(member.getId(), reqState).forEach(sellOrder -> {

            if (sellOrderStateRepository.isLastBySellOrderId(sellOrder.getId(), reqState)) {
                sellOrderResponses.add(
                        SellOrderResponse.builder()
                                .id(sellOrder.getId())
                                .orderNumber(sellOrder.getOrderNumber())
                                .name(sellOrder.getName())
                                .phoneNumber(sellOrder.getPhoneNumber())
                                .bank(sellOrder.getBank())
                                .bagQuantity(sellOrder.getBagQuantity())
                                .productQuantity(sellOrder.getProductQuantity())
                                .address(sellOrder.getAddress())
                                .requestDetail(sellOrder.getRequestDetail())
                                .returnDate(sellOrder.getReturnDate())
                                .sellState(reqState)
                                .createdDate(sellOrder.getCreatedDate())
                                .build()
                );
            }
        });

        return sellOrderResponses;

    }

    /**
     * 관리자가 모든 유저들의, 마지막 상태에 해당하는 판매 주문을 조회한다.
     * @return List<SellOrderResponse> (id, name, phoneNumber, bankName, accountNumber, bagQuantity, productQuantity, address, requestDetail, returnDate, sellState)
     * @author seochanhyeok
     */
    public List<SellOrderResponse> getAllSellOrdersAdmin(String state) {

        SellState reqState = Parser.parseSellState(state);

        List<SellOrderResponse> sellOrderResponses = new ArrayList<>();
        List<SellOrder> sellOrders = sellOrderRepository.getSellOrdersByState(reqState);
        sellOrders.forEach(sellOrder -> {
                if (sellOrderStateRepository.isLastBySellOrderId(sellOrder.getId(), reqState)) {
                    sellOrderResponses.add(
                            SellOrderResponse.builder()
                                    .id(sellOrder.getId())
                                    .name(sellOrder.getName())
                                    .orderNumber(sellOrder.getOrderNumber())
                                    .phoneNumber(sellOrder.getPhoneNumber())
                                    .bank(sellOrder.getBank())
                                    .bagQuantity(sellOrder.getBagQuantity())
                                    .productQuantity(sellOrder.getProductQuantity())
                                    .address(sellOrder.getAddress())
                                    .requestDetail(sellOrder.getRequestDetail())
                                    .returnDate(sellOrder.getReturnDate())
                                    .sellState(reqState)
                                    .createdDate(sellOrder.getCreatedDate())
                                    .build());
                }
        });

        return sellOrderResponses;
    }

    private void buildSellOrderState(SellOrder sellOrder, SellState sellState) {
        SellOrderState sellOrderState = SellOrderState.builder()
                .sellOrder(sellOrder)
                .sellState(sellState)
                .build();
        sellOrderStateRepository.save(sellOrderState);
    }

    /**
     * 관리자가 판매 요청을 업데이트함
     * @param request (orderNumber, sellState)
     * @return SellOrderResponse (id, name, phoneNumber, bankName, accountNumber, bagQuantity, productQuantity, address, requestDetail, returnDate, sellState)
     * @exception CustomException orderNumber에 해당하는 order를 조회하지 못할 경우 ORDER_NOT_FOUND "판매 요청을 찾을 수 없음" 에러 발생
     * @author seochanhyeok
     */
    public SellOrderResponse updateSellOrderState(SellOrderUpdateRequest request) {
        // 주문번호로 판매 주문을 가져온다.
        Optional<SellOrder> bySellOrder = sellOrderRepository.findByOrderNumber(request.getOrderNumber());
        if (bySellOrder.isEmpty()) {
            throw new CustomException(ORDER_NUMBER_NOT_FOUND);
        }
        SellOrder sellOrder = bySellOrder.get();

        // 판매 주문의 상태를 업데이트한다.
        buildSellOrderState(sellOrder, request.getSellState());

        return SellOrderResponse.builder()
                .id(sellOrder.getId())
                .name(sellOrder.getName())
                .orderNumber(sellOrder.getOrderNumber())
                .phoneNumber(sellOrder.getPhoneNumber())
                .bank(sellOrder.getBank())
                .bagQuantity(sellOrder.getBagQuantity())
                .productQuantity(sellOrder.getProductQuantity())
                .address(sellOrder.getAddress())
                .requestDetail(sellOrder.getRequestDetail())
                .returnDate(sellOrder.getReturnDate())
                .sellState(request.getSellState())
                .createdDate(sellOrder.getCreatedDate())
                .build();

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
}