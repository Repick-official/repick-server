package repick.repickserver.domain.order.application;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import repick.repickserver.domain.member.domain.Member;
import repick.repickserver.domain.order.dao.SellOrderRepository;
import repick.repickserver.domain.order.domain.SellOrder;
import repick.repickserver.domain.order.domain.SellState;
import repick.repickserver.domain.order.dto.SellOrderRequest;
import repick.repickserver.domain.order.dto.SellOrderResponse;
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
    private final JwtProvider jwtProvider;

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

            SellOrder sellOrder = SellOrder.builder()
                .name(request.getName())
                .phoneNumber(request.getPhoneNumber())
                .bankName(request.getBankName())
                .accountNumber(request.getAccountNumber())
                .bagQuantity(request.getBagQuantity())
                .productQuantity(request.getProductQuantity())
                .address(request.getAddress())
                .requestDetail(request.getRequestDetail())
                .returnDate(request.getReturnDate())
                .sellState(SellState.REQUESTED)
                .member(member)
                .build();

            sellOrderRepository.save(sellOrder);
            return true;

        } catch (Exception e) {
            System.out.println(e.getMessage());
            throw new CustomException(ORDER_FAIL);
        }

    }

    /**
     * 판매 조회
     * @param state (requested | canceled | delivered | published)
     * @param token (accessToken)
     * @return List<SellOrderResponse> (name, phoneNumber, bankName, accountNumber, bagQuantity, productQuantity, address, requestDetail, returnDate, sellState)
     * @exception CustomException (PATH_NOT_RESOLVED)
     * @author seochanhyeok
     */
    public List<SellOrderResponse> getSellOrders(String state, String token) {
        Member member = jwtProvider.getMemberByRawToken(token);

        SellState reqState;
        switch (state) {
            case "requested":
                reqState = SellState.REQUESTED;
                break;
            case "canceled":
                reqState = SellState.CANCELLED;
                break;
            case "delivered":
                reqState = SellState.DELIVERED;
                break;
            case "published":
                reqState = SellState.PUBLISHED;
                break;
            default:
                throw new CustomException(PATH_NOT_RESOLVED);
        }

        List<SellOrderResponse> sellOrderResponses = new ArrayList<>();
        List<SellOrder> sellOrders = sellOrderRepository.getSellOrders(member.getId(), reqState);
        sellOrders.forEach(sellOrder ->
                sellOrderResponses.add(
                    SellOrderResponse.builder()
                        .id(sellOrder.getId())
                        .name(sellOrder.getName())
                        .phoneNumber(sellOrder.getPhoneNumber())
                        .bankName(sellOrder.getBankName())
                        .accountNumber(sellOrder.getAccountNumber())
                        .bagQuantity(sellOrder.getBagQuantity())
                        .productQuantity(sellOrder.getProductQuantity())
                        .address(sellOrder.getAddress())
                        .requestDetail(sellOrder.getRequestDetail())
                        .returnDate(sellOrder.getReturnDate())
                        .sellState(sellOrder.getSellState())
                        .build())
                );

        return sellOrderResponses;


    }

    /**
     * 관리자가 모든 유저들의, 상태에 해당하는 '처리되지 않은' 판매 주문을 조회한다.
     * @return List<SellOrderResponse> (id, name, phoneNumber, bankName, accountNumber, bagQuantity, productQuantity, address, requestDetail, returnDate, sellState)
     * @apiNote
     * '처리되지 않은' 판매 주문이란,
     * 1. 판매 주문이 요청되었지만, 아직 처리되지 않은 주문
     * 2. 판매 주문이 배송되었지만, 아직 상품화를 거치지 않은 주문
     * 3. 모든 상품화 완료된 주문을 뜻한다.
     * @author seochanhyeok
     */
    public List<SellOrderResponse> getRequestedSellOrders(String state) {

        SellState reqState;
        switch (state) {
            case "requested":
                reqState = SellState.REQUESTED;
                break;
            case "canceled":
                reqState = SellState.CANCELLED;
                break;
            case "delivered":
                reqState = SellState.DELIVERED;
                break;
            case "published":
                reqState = SellState.PUBLISHED;
                break;
            default:
                throw new CustomException(PATH_NOT_RESOLVED);
        }

        List<SellOrderResponse> sellOrderResponses = new ArrayList<>();
        List<SellOrder> sellOrders = sellOrderRepository.getSellOrdersAdmin(reqState);
        sellOrders.forEach(sellOrder ->
                sellOrderResponses.add(
                        SellOrderResponse.builder()
                                .id(sellOrder.getId())
                                .name(sellOrder.getName())
                                .phoneNumber(sellOrder.getPhoneNumber())
                                .bankName(sellOrder.getBankName())
                                .accountNumber(sellOrder.getAccountNumber())
                                .bagQuantity(sellOrder.getBagQuantity())
                                .productQuantity(sellOrder.getProductQuantity())
                                .address(sellOrder.getAddress())
                                .requestDetail(sellOrder.getRequestDetail())
                                .returnDate(sellOrder.getReturnDate())
                                .parentSellOrder(sellOrder.getParentSellOrder())
                                .childSellOrders(sellOrder.getChildSellOrders())
                                .build())
        );

        return sellOrderResponses;
    }

    /**
     * 관리자가 판매 요청을 업데이트함
     * @param request (id, sellState)
     * @return SellOrder (orderId, name, phoneNumber, bankName, accountNumber, bagQuantity, productQuantity, address, requestDetail, returnDate, sellState)
     * @exception CustomException id에 해당하는 order를 조회하지 못할 경우 ORDER_NOT_FOUND "판매 요청을 찾을 수 없음" 에러 발생
     * @author seochanhyeok
     */
    public Boolean updateSellOrder(SellOrderRequest request) {
        // id를 받아서 해당 id의 sellOrder를 찾음
        SellOrder foundOrder = sellOrderRepository.findById(request.getId())
                .orElseThrow(() -> new CustomException(ORDER_NOT_FOUND));


        // request에서 받은 state로 sellOrder를 업데이트
        SellOrder sellOrder = SellOrder.builder()
                // parentSellOrder는 REQUESTED 상태인 SellOrder이다.
                .parentSellOrder(foundOrder)
                .name(foundOrder.getName())
                .phoneNumber(foundOrder.getPhoneNumber())
                .bankName(foundOrder.getBankName())
                .accountNumber(foundOrder.getAccountNumber())
                .bagQuantity(foundOrder.getBagQuantity())
                .productQuantity(foundOrder.getProductQuantity())
                .address(foundOrder.getAddress())
                .requestDetail(foundOrder.getRequestDetail())
                .returnDate(foundOrder.getReturnDate())
                .sellState(request.getSellState())
                .member(foundOrder.getMember())
                .build();

        sellOrderRepository.save(sellOrder);

        return true;
    }
}
