package me.snaptime.common.exception.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.io.DecodingException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import me.snaptime.common.dto.CommonResponseDto;
import me.snaptime.common.exception.customs.ExceptionCode;
import org.springframework.http.MediaType;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
public class JwtExceptionHandlerFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        try {
            filterChain.doFilter(request, response);
        } catch (DecodingException e) {
            setErrorResponse(response, ExceptionCode.TOKEN_INVALID_FORMAT);
            log.info("[JwtExceptionHandlerFilter] error name = DecodingException");
        } catch (MalformedJwtException e) {
            setErrorResponse(response, ExceptionCode.TOKEN_UNAUTHENTICATED);
            log.info("[JwtExceptionHandlerFilter] error name = MalformedJwtException");
        } catch (ExpiredJwtException e) {
            setErrorResponse(response, ExceptionCode.TOKEN_EXPIRED);
            log.info("[JwtExceptionHandlerFilter] error name = ExpiredJwtException");
        } catch (IllegalArgumentException e) {
            setErrorResponse(response, ExceptionCode.TOKEN_NOT_FOUND);
            log.info("[JwtExceptionHandlerFilter] error name = IllegalArgumentException");
        } catch (NullPointerException e) {
            setErrorResponse(response, ExceptionCode.TOKEN_NOT_FOUND);
            log.info("[JwtExceptionHandlerFilter] error name = NullPointerException");
        }

    }
    private void setErrorResponse(HttpServletResponse response,
                                  ExceptionCode exceptionMessage) {
        ObjectMapper objectMapper = new ObjectMapper();
        response.setStatus(exceptionMessage.getStatus().value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);

        CommonResponseDto commonResponse = new CommonResponseDto(exceptionMessage.getMessage(),null);
        try {
            response.getWriter().write(objectMapper.writeValueAsString(commonResponse));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
