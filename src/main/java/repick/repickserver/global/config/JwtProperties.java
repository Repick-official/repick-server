package repick.repickserver.global.config;


import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties("jwt")
@Data
public class JwtProperties {
    private String secret;
    private Long accessTokenExpirationTime;
    private Long refreshTokenExpirationTime;
    private String authoritiesKey;
}
