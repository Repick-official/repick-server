package repick.repickserver.infra.slack.application;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import repick.repickserver.domain.order.application.OrderService;
import repick.repickserver.domain.order.dto.UpdateOrderStateRequest;
import repick.repickserver.domain.subscriberinfo.application.SubscriberInfoService;
import repick.repickserver.domain.subscriberinfo.dto.SubscriberInfoRequest;
import repick.repickserver.domain.sellorder.application.SellOrderService;
import repick.repickserver.domain.sellorder.dto.SellOrderUpdateRequest;
import repick.repickserver.domain.sellorder.dto.UpdateSettlementStateRequest;
import repick.repickserver.domain.ordernumber.repository.OrderNumberReository;
import repick.repickserver.global.error.exception.CustomException;
import repick.repickserver.infra.slack.domain.WebHookType;
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

    private void validateOrderNumber(WebHookType webHookType, String orderNumber) {
        if (!orderNumberReository.existsByOrderNumber(orderNumber)) {
            slackNotifier.sendSlackNotification(webHookType, "명령에 실패했습니다. 주문번호가 올바르지 않습니다.");
            throw new CustomException(ORDER_NOT_FOUND);
        }
    }


    public void addSubscribeRequest(SlackApiSubscribeDto request) {
        validateOrderNumber(WebHookType.SUBSCRIBE, request.getOrderNumber());
        slackNotifier.sendSubscribeSlackNotification("구독 승인: " + request.getOrderNumber());
        subscriberInfoService.add(new SubscriberInfoRequest(request.getOrderNumber()));

    }

    public void denySubscribeRequest(SlackApiSubscribeDto request) {
        validateOrderNumber(WebHookType.SUBSCRIBE, request.getOrderNumber());
        slackNotifier.sendSubscribeSlackNotification("구독 거절: " + request.getOrderNumber());
        subscriberInfoService.deny(new SubscriberInfoRequest(request.getOrderNumber()));

    }

    public void updateSellRequest(SellOrderUpdateRequest request) {
        validateOrderNumber(WebHookType.SELL_ORDER, request.getOrderNumber());
        slackNotifier.sendSellOrderSlackNotification("판매주문 상태 변경: " + request.getOrderNumber() + " " + request.getSellState().toString());
        sellOrderService.updateSellOrderState(request);
    }

    public void updateOrderRequest(UpdateOrderStateRequest request) {
        validateOrderNumber(WebHookType.ORDER, request.getOrderNumber());
        slackNotifier.sendOrderSlackNotification("구매주문 상태 변경: " + request.getOrderNumber() + " " + request.getOrderState());
        orderService.updateOrderState(request);
    }

    public void updateSettlementRequest(UpdateSettlementStateRequest request) {
        validateOrderNumber(WebHookType.EXPENSE_SETTLEMENT, request.getProductNumber());
        slackNotifier.sendExpenseSettlementSlackNotification("정산 상태 변경: " + request.getProductNumber());
        sellOrderService.updateSettlementState(request);
    }
}
