package repick.repickserver.domain.order.application;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import repick.repickserver.domain.member.domain.Member;
import repick.repickserver.domain.order.dao.OrderRepository;
import repick.repickserver.domain.order.dao.SellInfoRepository;
import repick.repickserver.domain.order.domain.Order;
import repick.repickserver.domain.order.domain.OrderState;
import repick.repickserver.domain.order.domain.SellInfo;
import repick.repickserver.domain.order.dto.SellOrderRequest;
import repick.repickserver.domain.order.dto.SellOrderResponse;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

@Service
@Transactional
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final SellInfoRepository sellInfoRepository;

    public boolean postSellOrder(SellOrderRequest request, Member member) throws Exception {

        try {
            Order order = Order.builder()
                    .member(member)
                    .name(request.getName())
                    .address(request.getAddress())
                    .phoneNumber(request.getPhoneNumber())
                    .requestDetail(request.getRequestDetail())
                    .orderState(OrderState.SELL_ORDERED)
                    .build();

            SellInfo sellInfo = SellInfo.builder()
                    .order(order)
                    .bagQuantity(request.getBagQuantity())
                    .productQuantity(request.getProductQuantity())
                    .returnDate(request.getReturnDate())
                    .bankName(request.getBankName())
                    .accountNumber(request.getAccountNumber())
                    .build();

            orderRepository.save(order);
            sellInfoRepository.save(sellInfo);
            return true;

        } catch (Exception e) {
            System.out.println(e.getMessage());
            throw new Exception("주문 등록에 실패했습니다.");
        }


    }

    public List<SellOrderResponse> getSellOrders(Member member) {

        List<SellOrderResponse> sellOrderResponses = new ArrayList<>();

        Stream<SellInfo> sellInfoStream = sellInfoRepository.findAll()
                .stream().filter(m -> m.getOrder().getMember().getId().equals(member.getId()));

        sellInfoStream.forEach(sellInfo -> {
            SellOrderResponse sellOrderResponse = SellOrderResponse.builder()
                    .order(sellInfo.getOrder())
                    .bagQuantity(sellInfo.getBagQuantity())
                    .productQuantity(sellInfo.getProductQuantity())
                    .returnDate(sellInfo.getReturnDate())
                    .build();
            sellOrderResponses.add(sellOrderResponse);
        });

        return sellOrderResponses;

    }
}
