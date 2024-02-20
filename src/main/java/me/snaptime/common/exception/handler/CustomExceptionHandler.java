package me.snaptime.common.exception.handler;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.log4j.Log4j2;
import me.snaptime.common.dto.CommonResponseDto;
import me.snaptime.common.exception.customs.CustomException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;


@RestControllerAdvice
@Log4j2
public class CustomExceptionHandler extends ResponseEntityExceptionHandler{

    //@Valid 유효성검사 실패 시
    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        BindingResult bindingResult = ex.getBindingResult();
        Map<String, String> errors = new HashMap<>();
        bindingResult.getFieldErrors().forEach(error -> errors.put(error.getField(), error.getDefaultMessage()));

        log.error(errors.toString());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new CommonResponseDto("올바르지 않은 입력값입니다",errors));
    }

    // custom예외
    @ExceptionHandler(CustomException.class)
    public ResponseEntity<CommonResponseDto> handleCustomException(CustomException ex){
        log.error("예외가 발생했습니다.:"+ex.getExceptionCode().getMessage());
        return ResponseEntity.status(ex.getExceptionCode().getStatus()).body(new CommonResponseDto(ex.getExceptionCode().getMessage(),null));
    }

    // requestParam으로 입력받은 값의 유효성검사 실패
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<CommonResponseDto> handleContranintViolation(ConstraintViolationException ex){
        Map<String, String> errors = new HashMap<>();
        Set<ConstraintViolation<?>> violations = ex.getConstraintViolations();

        for (ConstraintViolation<?> violation : violations) {
            String fieldName = violation.getPropertyPath().toString();
            String message = violation.getMessage();

            if(fieldName.contains("fromUserName"))
                fieldName="fromUserName";
            errors.put(fieldName,message);
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new CommonResponseDto("올바르지 않은 입력값입니다.",errors));
    }

}
