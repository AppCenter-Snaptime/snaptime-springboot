package me.snaptime.exception.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import me.snaptime.common.CommonResponseDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Slf4j
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException {
        log.info("[commence] 인증에 실패하였습니다. : " + authException.getMessage());

        ObjectMapper objectMapper = new ObjectMapper();
        //HTTP 응답 헤더의 Content-Type을 JSON으로 설정합니다.
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        //HTTP 응답 상태 코드를 401 Unauthorized로 설정합니다.
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setCharacterEncoding("UTF-8");

        CommonResponseDto commonResponse = new CommonResponseDto("인증되지 않은 사용자입니다. 로그인을 수행하세요",null);
        try {
            response.getWriter().write(objectMapper.writeValueAsString(commonResponse));
        } catch (IOException e){
            e.printStackTrace();
        }
    }
}
