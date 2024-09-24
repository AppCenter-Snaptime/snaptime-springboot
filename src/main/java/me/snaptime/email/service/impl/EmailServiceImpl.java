package me.snaptime.email.service.impl;

import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import me.snaptime.component.email.EmailComponent;
import me.snaptime.component.redis.RedisComponent;
import me.snaptime.email.service.EmailService;
import me.snaptime.exception.CustomException;
import me.snaptime.exception.ExceptionCode;
import me.snaptime.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.util.Random;

@Service
@RequiredArgsConstructor
@Transactional
public class EmailServiceImpl implements EmailService {

    private final UserRepository userRepository;
    private final RedisComponent redisComponent;
    private final EmailComponent emailComponent;

    @Value("${spring.mail.auth-code-expiration-millis}")
    private long authCodeExpirationMillis;
    @Override
    public void sendCodeToEmail(String toEmail) throws MessagingException {
        // 이메일 중복 검사
        if(userRepository.existsByEmail(toEmail)) throw new CustomException(ExceptionCode.EMAIL_DUPLICATE);

        // 인증코드 생성, 저장 및 이메일 전송
        String verificationCode = createCode();
        // 이메일 인증 요청 시 인증 번호 Redis에 저장
        redisComponent.setValuesExpire(toEmail, verificationCode, Duration.ofMillis(authCodeExpirationMillis));
        emailComponent.sendVerificationCode(toEmail,verificationCode);
    }

    @Override
    public boolean verifyCode(String email, String verificationCode){
        String redisVerificationCode = redisComponent.getValues(email);
        return redisComponent.checkExistSValue(redisVerificationCode) && redisVerificationCode.equals(verificationCode);
    }

    private String createCode(){
        int leftLimit = 48; // 숫자 '0'
        int rightLimit = 122; // 알파벳 'z'
        int targetStringLength = 6;
        Random random = new Random();

        return random.ints(leftLimit, rightLimit + 1)  // leftLimit ~ rightLimit 사이의 난수 생성
                .filter(i -> (i >= 48 && i <= 57) ||  // 숫자 '0' ~ '9'
                        (i >= 65 && i <= 90) || // 대문자 'A' ~ 'Z'
                        (i >= 97 && i <= 122))  // 소문자 'a' ~ 'z'
                .limit(targetStringLength) // 길이 제한
                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append) // 객체 생성, 코드 포인트 StringBuilder에 추가, StringBuilder 객체 합치기
                .toString();
    }
}
