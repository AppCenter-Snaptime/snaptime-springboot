package me.snaptime.common.config.security.exception;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.OutputStream;

@Component
@Slf4j
public class CustomAccessDeniedHandler implements AccessDeniedHandler {

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException {
        log.info("[handle] 해당 리소스에 엑세스 할 권한이 없습니다.");

        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        //HTTP 응답 상태 코드를  403(FORBIDDEN)으로 설정합니다.
        response.setStatus(HttpStatus.FORBIDDEN .value());
        //try-with-resource 블록을 만들어, 블록을 빠져나가면 os  가 자동으로 닫히게 만듬.,
        //자원 누수를 방지하기 위한 좋은 방법.
        try (OutputStream os = response.getOutputStream()) {
            //Jackson 라이브러리의 ObjectMapper를 사용하여 JSON 형식의 에러 메시지를 생성합니다.
            ObjectMapper objectMapper = new ObjectMapper();
            //에러 메시지를 JSON 형식으로 작성하여 출력합니다.
            objectMapper.writeValue(os,"해당 리소스에 엑세스 할 권한이 없습니다.");
            //OutputStream을 비워내고 닫습니다.
            os.flush();
        }

    }
}
