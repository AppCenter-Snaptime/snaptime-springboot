package me.snaptime.exception.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import me.snaptime.common.CommonResponseDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Slf4j
public class CustomAccessDeniedHandler implements AccessDeniedHandler {

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException {
        log.info("[handle] 해당 리소스에 엑세스 할 권한이 없습니다.");

        ObjectMapper objectMapper = new ObjectMapper();
        //HTTP 응답 헤더의 Content-Type을 JSON으로 설정합니다.
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        //HTTP 응답 상태 코드를  403(FORBIDDEN)으로 설정합니다.
        response.setStatus(HttpStatus.FORBIDDEN.value());
        response.setCharacterEncoding("UTF-8");

        CommonResponseDto commonResponse = new CommonResponseDto("해당 리소스에 접근할 권한이 없습니다.", null);
        try {
            response.getWriter().write(objectMapper.writeValueAsString(commonResponse));
        }catch (IOException e){
            e.printStackTrace();
        }

    }
}
