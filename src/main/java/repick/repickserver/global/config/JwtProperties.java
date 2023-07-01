package repick.repickserver.global.config;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties("jwt")
@Data
public class JwtProperties {
    private String secret;
    private int accessTokenExpirationTime;
    private int refreshTokenExpirationTime;
    private String authoritiesKey;
}
