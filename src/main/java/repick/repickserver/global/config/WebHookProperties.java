package repick.repickserver.global.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties("webhook")
@Data
public class WebHookProperties {
    private String uri;
}
