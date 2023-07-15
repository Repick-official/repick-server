package repick.repickserver.global.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties("sweet-tracker")
@Data
public class SweetTrackerProperties {
    private String apiKey;
}
