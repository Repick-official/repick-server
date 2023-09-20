package repick.repickserver.global.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.servers.Server;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import repick.repickserver.global.properties.ServerProperties;
import springfox.documentation.oas.web.OpenApiTransformationContext;
import springfox.documentation.oas.web.WebMvcOpenApiTransformationFilter;
import springfox.documentation.spi.DocumentationType;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;


@Component
@RequiredArgsConstructor
public class Workaround implements WebMvcOpenApiTransformationFilter {

    private final ServerProperties serverProperties;


    @Override
    public OpenAPI transform(OpenApiTransformationContext<HttpServletRequest> context) {
        OpenAPI openApi = context.getSpecification();
        Server localServer = new Server();
        localServer.setDescription("local");
        localServer.setUrl("http://localhost:8080");

        Server developServer = new Server();
        developServer.setDescription("develop");
        developServer.setUrl(serverProperties.getServerAddress());
        openApi.setServers(Arrays.asList(localServer, developServer));

        return openApi;
    }

    @Override
    public boolean supports(DocumentationType documentationType) {
        return documentationType.equals(DocumentationType.OAS_30);
    }
}