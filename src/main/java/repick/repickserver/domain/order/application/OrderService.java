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

import javax.transaction.Transactional;

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
                    .address(request.getAddress())
                    .phoneNumber(request.getPhoneNumber())
                    .request_detail(request.getRequest_detail())
                    .orderState(OrderState.SELL_ORDERED)
                    .build();

            SellInfo sellInfo = SellInfo.builder()
                    .order(order)
                    .bagQuantity(request.getBagQuantity())
                    .productQuantity(request.getProductQuantity())
                    .returnDate(request.getReturnDate())
                    .build();

            orderRepository.save(order);
            sellInfoRepository.save(sellInfo);
            return true;

        } catch (Exception e) {
            System.out.println(e.getMessage());
            throw new Exception("주문 등록에 실패했습니다.");
        }


    }
}
