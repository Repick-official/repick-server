package repick.repickserver.global.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import repick.repickserver.global.jwt.JwtAuthenticationFilter;
import repick.repickserver.global.jwt.JwtProvider;

@Configuration
@RequiredArgsConstructor
@EnableWebSecurity
public class SecurityConfig {

    private final JwtProvider jwtProvider;


    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // ID, Password 문자열을 Base64로 인코딩하여 전달하는 구조
                .httpBasic().disable()
                // 쿠키 기반이 아닌 JWT 기반이므로 사용하지 않음
                .csrf().disable()
                .cors();
                // Spring Security 세션 정책 : 세션을 생성 및 사용하지 않음
                http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
                // 조건별로 요청 허용/제한 설정
                http.authorizeRequests()
                // 회원가입과 로그인은 모두 승인
                .antMatchers("/api/swagger-ui.html").permitAll()
                .antMatchers("/api/sign/register", "/sign/login").permitAll()
                .antMatchers("/api/sign/update").hasAuthority("USER")
                .antMatchers("/api/order/admin/**").hasAuthority("ADMIN")
                .antMatchers("/api/order/**").hasAuthority("USER")
                .antMatchers("/api/sell/admin/**").hasAuthority("ADMIN")
                .antMatchers("카카오 로그인 요청 API").permitAll()
                // S3 파일 업로드 요청은 모두 승인
                .antMatchers("/api/products/register").permitAll()
                .antMatchers("/api/s3/**").permitAll()
                .antMatchers("/api/subscribe/admin/**").hasAuthority("ADMIN")
                .antMatchers("/api/subscribe/**").hasAuthority("USER")
                .antMatchers("/api/cart/admin/**").hasAuthority("ADMIN")
                .antMatchers("/api/cart/**").hasAuthority("USER")
                .antMatchers(("/api/home-fitting/admin/**")).hasAuthority("ADMIN")
                .antMatchers(("/api/home-fitting/**")).hasAuthority("USER")
                .anyRequest().permitAll()
                .and()
                // JWT 인증 필터 적용
                .addFilterBefore(new JwtAuthenticationFilter(jwtProvider), UsernamePasswordAuthenticationFilter.class)
                // 에러 핸들링
                .exceptionHandling()
                .accessDeniedHandler((request, response, accessDeniedException) -> {
                    // 권한 문제가 발생했을 때 이 부분을 호출한다.
                    response.setStatus(403);
                    response.setCharacterEncoding("utf-8");
                    response.setContentType("text/html; charset=UTF-8");
                    response.getWriter().write("권한이 없는 사용자입니다.");
                })
                .authenticationEntryPoint((request, response, authException) -> {
                    // 인증문제가 발생했을 때 이 부분을 호출한다.
                    response.setStatus(401);
                    response.setCharacterEncoding("utf-8");
                    response.setContentType("text/html; charset=UTF-8");
                    response.getWriter().write("인증되지 않은 사용자입니다.");
                });

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

}