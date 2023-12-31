package repick.repickserver.infra.slack.application;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import repick.repickserver.domain.order.application.OrderService;
import repick.repickserver.domain.order.dto.UpdateOrderStateRequest;
import repick.repickserver.domain.sellorder.application.SellOrderService;
import repick.repickserver.domain.sellorder.dto.SellOrderUpdateRequest;
import repick.repickserver.domain.sellorder.dto.UpdateSettlementStateRequest;
import repick.repickserver.infra.slack.domain.WebHookType;
import repick.repickserver.infra.slack.validator.SlackApiValidator;

@Service
@RequiredArgsConstructor
public class SlackApiService {

    private final SlackNotifier slackNotifier;
    private final SellOrderService sellOrderService;
    private final OrderService orderService;
    private final SlackApiValidator slackApiValidator;

    public void updateSellRequest(SellOrderUpdateRequest request) {
        slackApiValidator.validateOrderNumber(WebHookType.SELL_ORDER, request.getOrderNumber());
        slackNotifier.sendSellOrderSlackNotification("판매주문 상태 변경: " + request.getOrderNumber() + " " + request.getSellState().toString());
        sellOrderService.updateSellOrderState(request);
    }

    public void updateOrderRequest(UpdateOrderStateRequest request) {
        slackApiValidator.validateOrderNumber(WebHookType.ORDER, request.getOrderNumber());
        slackNotifier.sendOrderSlackNotification("구매주문 상태 변경: " + request.getOrderNumber() + " " + request.getOrderState());
        orderService.updateOrderState(request);
    }

    public void updateSettlementRequest(UpdateSettlementStateRequest request) {
        slackApiValidator.validateOrderNumber(WebHookType.EXPENSE_SETTLEMENT, request.getProductNumber());
        slackNotifier.sendExpenseSettlementSlackNotification("정산 상태 변경: " + request.getProductNumber());
        sellOrderService.updateSettlementState(request);
    }
}
