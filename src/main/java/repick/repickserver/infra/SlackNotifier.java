package repick.repickserver.infra;

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

    public void sendSlackNotification(String message) {
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json;charset=utf-8");

        String payload = String.format("{\"text\": \"%s\"}", message);
        HttpEntity<String> request = new HttpEntity<>(payload, headers);

        ResponseEntity<String> response = restTemplate.exchange(webHookProperties.getUri(), HttpMethod.POST, request, String.class);
    }
}