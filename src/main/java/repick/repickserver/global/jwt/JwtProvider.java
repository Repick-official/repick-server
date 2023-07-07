package repick.repickserver.global.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import repick.repickserver.domain.member.dao.MemberRepository;
import repick.repickserver.domain.member.domain.Member;
import repick.repickserver.domain.member.domain.Role;
import repick.repickserver.global.config.JwtProperties;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;

@RequiredArgsConstructor
@Component
public class JwtProvider {

    @Autowired
    private JwtProperties jwtProperties;

    private Key secretKey;

    private final JpaUserDetailsService userDetailsService;
    private final MemberRepository memberRepository;

    @PostConstruct
    protected void init() {
        secretKey = Keys.hmacShaKeyFor(jwtProperties.getSecret().getBytes(StandardCharsets.UTF_8));
    }

    // access 토큰 생성
    public String createAccessToken(UserDetailsImpl userDetailsImpl) {
        return createToken(userDetailsImpl.getUser().getEmail(), userDetailsImpl.getUser().getRole(), jwtProperties.getAccessTokenExpirationTime());
    }

    // refresh 토큰 생성
    public String createRefreshToken(UserDetailsImpl userDetailsImpl) {
        return createToken(userDetailsImpl.getUser().getEmail(), userDetailsImpl.getUser().getRole(), jwtProperties.getRefreshTokenExpirationTime());
    }

    // 토큰 생성
    private String createToken(String email, Role role, Long expirationTime) {
        Claims claims = Jwts.claims().setSubject(email);
        claims.put("role", role);
        Date now = new Date();
        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime() + expirationTime))
                .signWith(secretKey, SignatureAlgorithm.HS256)
                .compact();
    }

    // 권한정보 획득
    // Spring Security 인증과정에서 권한확인을 위한 기능
    public Authentication getAuthentication(String token) {
        UserDetails userDetails = userDetailsService.loadUserByUsername(this.getEmail(token));
        return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
    }

    // 토큰에 담겨있는 유저 email 획득
    public String getEmail(String token) {
        return Jwts.parserBuilder().setSigningKey(secretKey).build().parseClaimsJws(token).getBody().getSubject();
    }

    /* 추가됨: getMember
        호출: RequestHeader("Authorization")의 String token을 그대로(Bearer 안떼고) 넣는다
        반환: 해당 Member 객체를 반환한다.
     */
    public Member getMemberByRawToken(String token) throws Exception {
        // 토큰으로부터 이메일을 얻음
        token = token.split(" ")[1].trim();
        String email = getEmail(token);
        // 이메일로 멤버 인스턴스를 얻음
        return memberRepository.findByEmail(email).orElseThrow(Exception::new);
    }

    // Authorization Header를 통해 인증을 한다.
    public String resolveToken(HttpServletRequest request) {
        return request.getHeader("Authorization");
    }

    // 토큰 검증
    public boolean validateToken(String token) {
        try {
            // Bearer 검증
            if (!token.substring(0, "BEARER ".length()).equalsIgnoreCase("BEARER ")) {
                return false;
            } else {
                token = token.split(" ")[1].trim();
            }
            Jws<Claims> claims = Jwts.parserBuilder().setSigningKey(secretKey).build().parseClaimsJws(token);

            // TODO : 토큰 종류 검사

            // 만료되었을 시 false
            return !claims.getBody().getExpiration().before(new Date());
        } catch (Exception e) {
            return false;
        }
    }
}