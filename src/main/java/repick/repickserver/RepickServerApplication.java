package repick.repickserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableAsync;
import repick.repickserver.global.config.JwtProperties;

@SpringBootApplication
@EnableAsync
@EnableJpaAuditing
@EnableConfigurationProperties(JwtProperties.class)
public class RepickServerApplication {

	public static void main(String[] args) {
		SpringApplication.run(RepickServerApplication.class, args);
	}

}
