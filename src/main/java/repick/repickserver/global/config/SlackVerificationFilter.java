package repick.repickserver.global.config;

import org.apache.commons.codec.digest.HmacAlgorithms;
import org.apache.commons.codec.digest.HmacUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class SlackVerificationFilter extends OncePerRequestFilter {

    @Value("${slack.signing-secret}")
    private String signingSecret;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String timestamp = request.getHeader("X-Slack-Request-Timestamp");
        String requestBody = request.getReader().lines()
                .reduce("", (accumulator, actual) -> accumulator + actual);
        String baseString = "v0:" + timestamp + ":" + requestBody;

        String mySignature = "v0=" + new HmacUtils(HmacAlgorithms.HMAC_SHA_256, signingSecret).hmacHex(baseString);
        String slackSignature = request.getHeader("X-Slack-Signature");

        if (!mySignature.equals(slackSignature)) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        filterChain.doFilter(request, response);
    }
}
