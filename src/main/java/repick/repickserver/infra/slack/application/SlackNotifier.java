package repick.repickserver.infra.slack.application;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import repick.repickserver.global.config.WebHookProperties;

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

    public void sendSubscribeSlackNotification(String message) {
        RestTemplate restTemplate = new RestTemplate();
        HttpEntity<String> request = createRequest(message);

        ResponseEntity<String> response = restTemplate.exchange(webHookProperties.getSubscribeUri(), HttpMethod.POST, request, String.class);
    }

    public void sendSellOrderSlackNotification(String message) {
        RestTemplate restTemplate = new RestTemplate();
        HttpEntity<String> request = createRequest(message);

        ResponseEntity<String> response = restTemplate.exchange(webHookProperties.getSellOrderUri(), HttpMethod.POST, request, String.class);
    }

    public void sendHomeFittingSlackNotification(String message) {
        RestTemplate restTemplate = new RestTemplate();
        HttpEntity<String> request = createRequest(message);

        ResponseEntity<String> response = restTemplate.exchange(webHookProperties.getHomeFittingUri(), HttpMethod.POST, request, String.class);
    }

    public void sendOrderSlackNotification(String message) {
        RestTemplate restTemplate = new RestTemplate();
        HttpEntity<String> request = createRequest(message);

        ResponseEntity<String> response = restTemplate.exchange(webHookProperties.getOrderUri(), HttpMethod.POST, request, String.class);
    }

    public void sendExpenseSettlementSlackNotification(String message) {
        RestTemplate restTemplate = new RestTemplate();
        HttpEntity<String> request = createRequest(message);

        ResponseEntity<String> response = restTemplate.exchange(webHookProperties.getExpenseSettlementUri(), HttpMethod.POST, request, String.class);
    }
}