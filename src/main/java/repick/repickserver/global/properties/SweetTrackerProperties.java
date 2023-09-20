package repick.repickserver.global.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties("sweet-tracker")
@Data
public class SweetTrackerProperties {
    private String apiKey;
}
