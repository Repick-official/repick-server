package repick.repickserver.infra.slack.api;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import repick.repickserver.domain.cart.dto.UpdateOrderStateRequest;
import repick.repickserver.domain.order.dto.SellOrderUpdateRequest;
import repick.repickserver.domain.order.dto.UpdateSettlementStateRequest;
import repick.repickserver.infra.slack.application.SlackApiService;
import repick.repickserver.infra.slack.dto.SlackApiSubscribeDto;

@RestController
@RequestMapping("/slack")
@RequiredArgsConstructor
public class SlackApiController {

    private final SlackApiService slackApiService;

    @PostMapping(value = "/subscribe/add")
    public void addSubscribeRequest(@RequestBody SlackApiSubscribeDto request) {
        slackApiService.addSubscribeRequest(request);
    }

    @PostMapping(value = "/subscribe/deny")
    public void denySubscribeRequest(@RequestBody SlackApiSubscribeDto request) {
        slackApiService.denySubscribeRequest(request);
    }

    @PostMapping(value = "/sell/update")
    public void updateSellRequest(@RequestBody SellOrderUpdateRequest request) {
        slackApiService.updateSellRequest(request);
    }

    @PostMapping(value = "/order/update")
    public void updateOrderRequest(@RequestBody UpdateOrderStateRequest request) {
        slackApiService.updateOrderRequest(request);
    }

    @PostMapping(value = "/settlement/update")
    public void updateSettlementRequest(@RequestBody UpdateSettlementStateRequest request) {
        slackApiService.updateSettlementRequest(request);
    }

}
