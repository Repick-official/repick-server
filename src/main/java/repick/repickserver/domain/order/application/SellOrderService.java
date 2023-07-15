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
import repick.repickserver.domain.ordernumber.application.OrderNumberService;
import repick.repickserver.domain.ordernumber.domain.OrderType;
import repick.repickserver.global.Parser;
import repick.repickserver.global.error.exception.CustomException;
import repick.repickserver.global.jwt.JwtProvider;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;

import static repick.repickserver.global.error.exception.ErrorCode.*;

@Service
@Transactional
@RequiredArgsConstructor
public class SellOrderService {

    private final SellOrderRepository sellOrderRepository;
    private final SellOrderStateRepository sellOrderStateRepository;
    private final JwtProvider jwtProvider;
    private final OrderNumberService orderNumberService;

    /**
     * 판매 수거 요청
     * @param request (name, phoneNumber, bankName, accountNumber, bagQuantity, productQuantity, address, requestDetail, returnDate)
     * @param token (accessToken)
     * @return true
     * @exception CustomException (ORDER_FAIL)
     * @author seochanhyeok
     */
    public boolean postSellOrder(SellOrderRequest request, String token) {
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

            return true;

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
                                    .build());
                }
        });

        return sellOrderResponses;
    }

    /**
     * 관리자가 판매 요청을 업데이트함
     * @param request (orderNumber, sellState)
     * @return true
     * @exception CustomException orderNumber에 해당하는 order를 조회하지 못할 경우 ORDER_NOT_FOUND "판매 요청을 찾을 수 없음" 에러 발생
     * @author seochanhyeok
     */
    public Boolean updateSellOrderAdmin(SellOrderUpdateRequest request) {
        // 주문번호로 판매 주문을 가져온다.
        SellOrder sellOrderList = sellOrderRepository.findByOrderNumber(request.getOrderNumber());

        // 해당 판매 주문이 없으면 에러를 던진다.
        if (sellOrderList == null) {
            throw new CustomException(ORDER_NOT_FOUND);
        }

        // 판매 주문의 상태를 업데이트한다.
        sellOrderStateRepository.save(SellOrderState.builder()
                .sellOrder(sellOrderList)
                .sellState(request.getSellState())
                .build());

        return true;

    }
}