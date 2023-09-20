package repick.repickserver.global.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties("webhook")
@Data
public class WebHookProperties {
    private String sellOrderUri;
    private String orderUri;
    private String expenseSettlementUri;
}
