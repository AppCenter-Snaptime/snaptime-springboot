package me.snaptime.common.config.security;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;

//Spring Security 에서 사용되는 비밀번호 인코더를 빈으로 등록하는 설정 클래스
//암호화된 비밀번호를 사용하여 사용자 인증 및 인가를 처리하는데, 이 때 비밀번호를 안전하게 저장하고 비교하기 위해 사용
@Configuration
public class PasswordEncoderConfiguration {

    //PasswordEncoderFactories 팩트리 클래스의 creatDelegatingPasswordEncoder()메서드를 사용하여 여러 암호화 전략 중 하나를 선택하고,
    //생성된 'PasswordEncoder' 를 반환합니다. 기본적으로 Bcrypt 알고리즘을 사용합니다.
    @Bean
    public PasswordEncoder passwordEncoder(){

        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }
}
