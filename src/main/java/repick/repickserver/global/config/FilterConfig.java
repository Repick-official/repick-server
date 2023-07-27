package repick.repickserver.global.config;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FilterConfig {

    @Bean
    public FilterRegistrationBean<SlackVerificationFilter> slackVerificationFilter(){
        FilterRegistrationBean<SlackVerificationFilter> registrationBean
                = new FilterRegistrationBean<>();

        registrationBean.setFilter(new SlackVerificationFilter());
        registrationBean.addUrlPatterns("/subscribe/admin/autoadd");

        return registrationBean;
    }
}
