package me.snaptime.common.jwt;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtProvider jwtProvider;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        log.info("[doFilterInternal] 토큰 가져오기");
        String token = jwtProvider.getAuthorizationToken(request);
        log.info("[doFilterInternal] Token ={}", token);

        if(token !=null && jwtProvider.validateToken(token)){
            Authentication authentication = jwtProvider.getAuthentication(token);
            SecurityContextHolder.getContext().setAuthentication(authentication);
            log.info("[doFilterInternal] 토큰 값 유효성 검증 완료");
        }

        //doFilter() 메서드를 기준으로 앞에 작성한 코드는 서블릿이 실행되기 전에 실행되고, 뒤에 작성한 코드는 서블릿이 실행된 후에 실행.
        filterChain.doFilter(request,response);
    }
}
