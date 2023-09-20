package repick.repickserver.global.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@ConfigurationProperties("ncp.sms")
@Component
@Data
public class SmsProperties {
    private String accessKey;
    private String secretKey;
    private String serviceId;
    private String sendFrom;
    private String bankName;
    private Long bankAccount;
}
