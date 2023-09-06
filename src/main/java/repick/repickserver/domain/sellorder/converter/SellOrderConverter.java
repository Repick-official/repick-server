package repick.repickserver.domain.sellorder.converter;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import repick.repickserver.domain.member.domain.Member;
import repick.repickserver.domain.sellorder.domain.SellOrder;
import repick.repickserver.domain.sellorder.dto.SellOrderRequest;
import repick.repickserver.domain.sellorder.dto.SellOrderResponse;

@Service @RequiredArgsConstructor
public class SellOrderConverter {

    public SellOrder toSellOrder(SellOrderRequest request, String orderNumber, Member member) {
        return SellOrderRequest.toSellOrder(request, orderNumber, member);
    }

    public SellOrderResponse toSellOrderResponse(SellOrder sellOrder) {
        return SellOrderResponse.from(sellOrder);
    }
}
