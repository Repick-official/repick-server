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

import static repick.repickserver.global.error.exception.ErrorCode.ORDER_FAIL;
import static repick.repickserver.global.error.exception.ErrorCode.PATH_NOT_RESOLVED;

@Service
@Transactional
@RequiredArgsConstructor
public class SellOrderService {

    private final SellOrderRepository sellOrderRepository;
    private final JwtProvider jwtProvider;

    public boolean postSellOrder(SellOrderRequest request, String token) throws Exception {
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

    public List<SellOrderResponse> getSellOrders(String state, String token) throws Exception {
        Member member = jwtProvider.getMemberByRawToken(token);

        System.out.println("member = " + member.toString());
        System.out.println("state = " + state);

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
                        .name(sellOrder.getName())
                        .phoneNumber(sellOrder.getPhoneNumber())
                        .bankName(sellOrder.getBankName())
                        .accountNumber(sellOrder.getAccountNumber())
                        .bagQuantity(sellOrder.getBagQuantity())
                        .productQuantity(sellOrder.getProductQuantity())
                        .address(sellOrder.getAddress())
                        .requestDetail(sellOrder.getRequestDetail())
                        .returnDate(sellOrder.getReturnDate())
                        .build())
                );

        return sellOrderResponses;


    }
}
