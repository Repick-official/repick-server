package repick.repickserver.domain.sellorder.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import repick.repickserver.domain.member.domain.Member;
import repick.repickserver.domain.sellorder.dao.SellOrderStateRepository;
import repick.repickserver.domain.sellorder.domain.SellOrder;
import repick.repickserver.domain.sellorder.dto.SellOrderRequest;
import repick.repickserver.domain.sellorder.dto.SellOrderResponse;

import java.time.LocalDateTime;

@Service @RequiredArgsConstructor
public class SellOrderMapper {

    private final SellOrderStateRepository sellOrderStateRepository;

    public SellOrder toSellOrder(SellOrderRequest request, String orderNumber, Member member) {
        return SellOrder.builder()
                .name(request.getName())
                .orderNumber(orderNumber)
                .phoneNumber(request.getPhoneNumber())
                .bank(request.getBank())
                .bagQuantity(request.getBagQuantity())
                .productQuantity(request.getProductQuantity())
                .address(request.getAddress())
                .requestDetail(request.getRequestDetail())
                // returnDate는 'yyyy-MM-dd' 형식 문자열으로 들어옴
                .returnDate(LocalDateTime.parse(request.getReturnDate() + "T00:00:00"))
                .member(member)
                .build();
    }

    public SellOrderResponse toSellOrderResponse(SellOrder sellOrder) {
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
                // 가장 최근에 업데이트된 state 가져옴
                .sellState(sellOrderStateRepository.findLastStateBySellOrderId(sellOrder.getId()).getSellState())
                .createdDate(sellOrder.getCreatedDate())
                .build();
    }
}
