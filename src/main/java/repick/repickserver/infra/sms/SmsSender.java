package repick.repickserver.infra.sms;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.apache.commons.codec.binary.Base64;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import repick.repickserver.global.config.SmsProperties;
import repick.repickserver.infra.sms.model.Message;
import repick.repickserver.infra.sms.model.SmsRequest;
import repick.repickserver.infra.sms.model.SmsResponse;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SmsSender {

    private final SmsProperties smsProperties;

    public SmsResponse sendSms(Message message) throws JsonProcessingException, UnsupportedEncodingException, InvalidKeyException, NoSuchAlgorithmException,
            URISyntaxException, RestClientException {

        Long time = System.currentTimeMillis();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("x-ncp-apigw-timestamp", time.toString());
        headers.set("x-ncp-iam-access-key", smsProperties.getAccessKey());
        headers.set("x-ncp-apigw-signature-v2", makeSignature(time));

        List<Message> messages = new ArrayList<>();
        messages.add(message);

        SmsRequest smsRequest = SmsRequest.builder()
                .type("LMS")
                .contentType("COMM")
                .countryCode("82")
                .from(smsProperties.getSendFrom())
                .content("[Repick]")
                .messages(messages)
                .build();

        ObjectMapper objectMapper = new ObjectMapper();
        String body = objectMapper.writeValueAsString(smsRequest);

        HttpEntity<String> httpRequest = new HttpEntity<>(body, headers);

        RestTemplate restTemplate = new RestTemplate();
        SmsResponse smsResponse = restTemplate.postForObject(new URI("https://sens.apigw.ntruss.com/sms/v2/services/" + smsProperties.getServiceId() + "/messages"),
                httpRequest, SmsResponse.class);

        return smsResponse;
    }

    public String makeSignature(Long time) throws NoSuchAlgorithmException, UnsupportedEncodingException, InvalidKeyException {
        String space = " ";
        String newLine = "\n";
        String method = "POST";
        String url = "/sms/v2/services/" + smsProperties.getServiceId() + "/messages";
        String timestamp = time.toString();
        String accessKey = smsProperties.getAccessKey();
        String secretKey = smsProperties.getSecretKey();

        String message = new StringBuilder()
                .append(method)
                .append(space)
                .append(url)
                .append(newLine)
                .append(timestamp)
                .append(newLine)
                .append(accessKey)
                .toString();

        SecretKeySpec signingKey = new SecretKeySpec(secretKey.getBytes("UTF-8"), "HmacSHA256");
        Mac mac = Mac.getInstance("HmacSHA256");
        mac.init(signingKey);

        byte[] rawHmac = mac.doFinal(message.getBytes("UTF-8"));
        String encodeBase64String = Base64.encodeBase64String(rawHmac);

        return encodeBase64String;
    }
}
