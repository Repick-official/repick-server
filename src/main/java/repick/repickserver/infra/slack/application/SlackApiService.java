package repick.repickserver.infra.slack.application;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import repick.repickserver.domain.cart.application.OrderService;
import repick.repickserver.domain.cart.dto.UpdateOrderStateRequest;
import repick.repickserver.domain.member.application.SubscriberInfoService;
import repick.repickserver.domain.member.dto.SubscriberInfoRequest;
import repick.repickserver.domain.order.application.SellOrderService;
import repick.repickserver.domain.order.dto.SellOrderUpdateRequest;
import repick.repickserver.domain.order.dto.UpdateSettlementStateRequest;
import repick.repickserver.domain.ordernumber.dao.OrderNumberReository;
import repick.repickserver.global.error.exception.CustomException;
import repick.repickserver.infra.slack.dto.SlackApiSubscribeDto;

import static repick.repickserver.global.error.exception.ErrorCode.*;

@Service
@RequiredArgsConstructor
public class SlackApiService {

    private final SlackNotifier slackNotifier;
    private final OrderNumberReository orderNumberReository;
    private final SubscriberInfoService subscriberInfoService;
    private final SellOrderService sellOrderService;
    private final OrderService orderService;

    private void validateOrderNumber(String orderNumber) {
        if (!orderNumberReository.existsByOrderNumber(orderNumber)) {
            slackNotifier.sendSubscribeSlackNotification("명령에 실패했습니다. 주문번호가 올바르지 않습니다.");
            throw new CustomException(ORDER_NOT_FOUND);
        }
    }


    public void addSubscribeRequest(SlackApiSubscribeDto request) {
        validateOrderNumber(request.getOrderNumber());
        slackNotifier.sendSubscribeSlackNotification("구독 승인: " + request.getOrderNumber());
        subscriberInfoService.add(new SubscriberInfoRequest(request.getOrderNumber()));

    }

    public void denySubscribeRequest(SlackApiSubscribeDto request) {
        validateOrderNumber(request.getOrderNumber());
        slackNotifier.sendSubscribeSlackNotification("구독 거절: " + request.getOrderNumber());
        subscriberInfoService.deny(new SubscriberInfoRequest(request.getOrderNumber()));

    }

    public void updateSellRequest(SellOrderUpdateRequest request) {
        validateOrderNumber(request.getOrderNumber());
        slackNotifier.sendSellOrderSlackNotification("판매주문 상태 변경: " + request.getOrderNumber() + " " + request.getSellState().toString());
        sellOrderService.updateSellOrderState(request);
    }

    public void updateOrderRequest(UpdateOrderStateRequest request) {
        validateOrderNumber(request.getOrderNumber());
        slackNotifier.sendOrderSlackNotification("구매주문 상태 변경: " + request.getOrderNumber() + " " + request.getOrderState());
        orderService.updateOrderState(request);
    }

    public void updateSettlementRequest(UpdateSettlementStateRequest request) {
        validateOrderNumber(request.getProductNumber());
        slackNotifier.sendExpenseSettlementSlackNotification("정산 상태 변경: " + request.getProductNumber());
        sellOrderService.updateSettlementState(request);
    }
}
