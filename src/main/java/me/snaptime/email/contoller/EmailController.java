package me.snaptime.email.contoller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.snaptime.common.CommonResponseDto;
import me.snaptime.email.service.EmailService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name ="[Email] Email API", description = "회원가입을 위한 이메일 전송, 이메일 인증")
@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/emails")
public class EmailController {

    private final EmailService emailService;

    @PostMapping("/send")
    @Operation(summary = "이메일 인증코드 발송", description = "email로 인증코드를 전송합니다.")
    public ResponseEntity<CommonResponseDto<String>> mailSend(@RequestParam String email) throws MessagingException {
        log.info("EmailController.mailSend()");
        emailService.sendCodeToEmail(email);
        return ResponseEntity.status(HttpStatus.OK).body(
                new CommonResponseDto<>("인증코드가 성공적으로 발송되었습니다.", email));
    }

    @PostMapping("/verify")
    @Operation(summary = "인증코드를 확인합니다.", description = "이메일과 인증코드를 통해서, 해당 이메일로 보낸 인증코드의 유효를 검증합니다.")
    public ResponseEntity<CommonResponseDto<Boolean>> verify(@RequestParam String email, @RequestParam String code) {
        log.info("EmailController.verify()");
        boolean isVerified = emailService.verifyCode(email, code);

        return ResponseEntity.status(HttpStatus.OK).body(
                new CommonResponseDto<>(isVerified ? "인증에 성공하였습니다" : "인증에 실패하였습니다",isVerified));
    }
}
