package me.snaptime.jwt;

import io.jsonwebtoken.*;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.snaptime.exception.CustomException;
import me.snaptime.exception.ExceptionCode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Date;
import java.util.List;

@Component
@Slf4j
@RequiredArgsConstructor
public class JwtProvider {

    private final UserDetailsServiceImpl userDetailsService;

    @Value("${spring.jwt.secret}")
    private String secretKey;

    @Value("${spring.jwt.access-token-valid-time}")
    private Long accessTokenValidTime;

    @Value("${spring.jwt.refresh-token-valid-time}")
    private Long refreshTokenValidTime;

    private Long testAccessTokenValidTime = 30000L;
    private Long testRefreshTokenValidTime = 60000L;

    @PostConstruct
    protected void init(){
        secretKey = Base64.getEncoder().encodeToString(secretKey.getBytes(StandardCharsets.UTF_8));
    }

    public String createAccessToken(Long userId, String email, List<String> roles){
        Claims claims = (Claims) Jwts.claims().setSubject(email);
        claims.put("userId",userId);
        claims.put("type","access");
        claims.put("roles",roles);
        Date now = new Date();
        String token = Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime() + accessTokenValidTime))
                .signWith(SignatureAlgorithm.HS256, secretKey)
                .compact();

        return token;
    }

    public String createRefreshToken(Long id, String email, List<String> roles){
        Claims claims = Jwts.claims().setSubject(email);
        claims.put("userId", id);
        claims.put("type", "refresh");
        claims.put("roles", roles);
        Date now = new Date();
        String token = Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime() + refreshTokenValidTime))
                .signWith(SignatureAlgorithm.HS256, secretKey)
                .compact();
        return token;
    }

    public String testCreateAccessToken(Long userId, String email, List<String> roles){
        Claims claims = Jwts.claims().setSubject(email);
        claims.put("userId",userId);
        claims.put("type","testAccess");
        claims.put("roles",roles);
        Date now = new Date();
        String token = Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime() + testAccessTokenValidTime))
                .signWith(SignatureAlgorithm.HS256, secretKey)
                .compact();

        return token;
    }

    public String testCreateRefreshToken(Long id, String email, List<String> roles){
        Claims claims = (Claims) Jwts.claims().setSubject(email);
        claims.put("userId", id);
        claims.put("type", "testRefresh");
        claims.put("roles", roles);
        Date now = new Date();
        String token = Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime() + testRefreshTokenValidTime))
                .signWith(SignatureAlgorithm.HS256, secretKey)
                .compact();
        return token;
    }

    public Long getUserId(String token) {
        Long userId = Long.valueOf(Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody().get("userId").toString());
        log.info("[getUserId] 토큰 기반 회원 구별 정보 추출 완료, userId : {}", userId);
        return userId;
    }

    // 필터에서 인증 성공 후, SecurityContextHolder 에 저장할 Authentication 을 생성
    //UsernamePasswordAuthenticationToken 클래스를 사용
    public Authentication getAuthentication(String token){
        UserDetails userDetails = userDetailsService.loadUserByUsername(this.getUsername(token));
        log.info("[getAuthentication] 토큰 인증 정보 조회 완료, UserDetails loginId : {}",userDetails.getUsername());

        return new UsernamePasswordAuthenticationToken(userDetails,"",userDetails.getAuthorities());
    }

    //토큰 클래스를 사용하기 위해 초기화를 위한 UserDetails가 필요하다.
    //Jwts.parser()를 통해 secretKey를 설정하고 클레임을 추출해서 토큰을 생성할 때 넣었던 sub값을 추출합니다.
    public String getUsername(String token)
    {
        String email = Jwts.parser()
                .setSigningKey(secretKey)
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
        log.info("[getUsername] 토큰 기반 회원 구별 정보 추출 완료, loginId : {}", email);
        return email;
    }

    public String getAuthorizationToken(HttpServletRequest request){
        String token = request.getHeader("Authorization");
        try{
            if(!token.substring(0,"BEARER ".length()).equalsIgnoreCase("Bearer ")){
                throw new IllegalStateException("Token 정보가 존재하지 않습니다.");
            }
            token = token.split(" ")[1].trim();
        }catch (Exception e){
            return null;
        }
        return token;
    }

    /*
        이 메소드는 토큰을 전달 받아 클레임의 유효기간을 체크하고 boolean 타입 값을 리턴하는 역할을 한다.
    */
    public boolean validateToken(String token) {
        try{
            Claims claims = getClaims(token);
            Date expiration = claims.getExpiration();
            if(expiration.before(new Date())){
                String tokenType = claims.get("type", String.class);
                if ("refresh".equals(tokenType) || "testRefresh".equals(tokenType)) {
                    throw new CustomException(ExceptionCode.REFRESH_TOKEN_EXPIRED);
                }
                throw new CustomException(ExceptionCode.ACCESS_TOKEN_EXPIRED);
            }
            return true;
        }catch (CustomException ex){
            throw ex;
        }
        catch (Exception e){
            log.info("[validateToken] 토큰 유효 체크 예외 발생");
            return false;
        }
    }

    private Claims getClaims(String token) {
        JwtParser jwtParser = Jwts.parser().setSigningKey(secretKey);
        try {
            // Try to parse claims
            return jwtParser.parseClaimsJws(token).getBody();
        } catch (ExpiredJwtException ex) {
            // If the token is expired, return the claims from the exception
            return ex.getClaims();
        } catch (Exception e) {
            // Handle other token parsing issues
            log.info("[validateToken] 토큰 유효 체크 예외 발생");
            throw new CustomException(ExceptionCode.TOKEN_INVALID);
        }
    }
}






