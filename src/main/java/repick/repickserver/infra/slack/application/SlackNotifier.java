package repick.repickserver.infra.slack.application;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import repick.repickserver.global.config.WebHookProperties;
import repick.repickserver.infra.slack.domain.WebHookType;

@Service
@RequiredArgsConstructor
public class SlackNotifier {

    private final WebHookProperties webHookProperties;

    private HttpEntity<String> createRequest(String message) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json;charset=utf-8");

        String payload = String.format("{\"text\": \"%s\"}", message);
        return new HttpEntity<>(payload, headers);
    }

    private String getSubscribeUri(WebHookType webHookType) {
        switch (webHookType) {
            case SUBSCRIBE:
                return webHookProperties.getSubscribeUri();
            case SELL_ORDER:
                return webHookProperties.getSellOrderUri();
            case HOME_FITTING:
                return webHookProperties.getHomeFittingUri();
            case ORDER:
                return webHookProperties.getOrderUri();
            case EXPENSE_SETTLEMENT:
                return webHookProperties.getExpenseSettlementUri();
            default:
                throw new IllegalArgumentException("Not supported WebHookType");
        }
    }

    @Async
    public void sendSlackNotification(WebHookType webHookType, String message) {
        RestTemplate restTemplate = new RestTemplate();
        HttpEntity<String> request = createRequest(message);

        ResponseEntity<String> response = restTemplate.exchange(getSubscribeUri(webHookType), HttpMethod.POST, request, String.class);
    }

    public void sendSubscribeSlackNotification(String message) {
        sendSlackNotification(WebHookType.SUBSCRIBE, message);
    }

    public void sendSellOrderSlackNotification(String message) {
        sendSlackNotification(WebHookType.SELL_ORDER, message);
    }

    public void sendHomeFittingSlackNotification(String message) {
        sendSlackNotification(WebHookType.HOME_FITTING, message);
    }

    public void sendOrderSlackNotification(String message) {
        sendSlackNotification(WebHookType.ORDER, message);
    }

    public void sendExpenseSettlementSlackNotification(String message) {
        sendSlackNotification(WebHookType.EXPENSE_SETTLEMENT, message);
    }
}