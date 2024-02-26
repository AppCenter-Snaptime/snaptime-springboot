package me.snaptime.common.config.security;

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
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtProvider jwtProvider;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        //JwtProvider 클래스를 통해, request 에서 토큰을 추출하고, 토큰에 대한 유효성을 검사
        String token = jwtProvider.resolveToken(request);
        log.info("[doFilterInternal] 요청 URL "+request.getRequestURL());
        log.info("[doFilterInternal] token 값 유효성 체크 시작");

        // 토큰이 유효하다면 Authentication 객체를 생성해서 SecurityContextHolder에 대한 유효성을 검사.
        if(token !=null && jwtProvider.validateToken(token)){
            Authentication authentication = jwtProvider.getAuthentication(token);
            //현재 스레드의 보안 컨텍스트에 사용자의 인증 정보를 설정하는 행동
            //해당 사용자의 보안 정보를 애플리케이션에서 사용하거나 검증할 수 있게 된다.
            SecurityContextHolder.getContext().setAuthentication(authentication);
            log.info("[doFilterInternal] token 값 유효성 체크 완료");
        }

        //doFilter() 메서드를 기준으로 앞에 작성한 코드는 서블릿이 실행되기 전에 실행되고, 뒤에 작성한 코드는 서블릿이 실행된 후에 실행.
        filterChain.doFilter(request,response);

    }
}
