package repick.repickserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import repick.repickserver.global.config.JwtProperties;

@SpringBootApplication
@EnableConfigurationProperties(JwtProperties.class)
public class RepickServerApplication {

	public static void main(String[] args) {
		SpringApplication.run(RepickServerApplication.class, args);
	}

}
