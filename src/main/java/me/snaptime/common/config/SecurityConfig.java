package me.snaptime.common.config;

import lombok.RequiredArgsConstructor;
import me.snaptime.common.exception.handler.JwtExceptionHandlerFilter;
import me.snaptime.common.exception.security.CustomAccessDeniedHandler;
import me.snaptime.common.exception.security.CustomAuthenticationEntryPoint;
import me.snaptime.common.jwt.JwtAuthFilter;
import me.snaptime.common.jwt.JwtProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;


@Configuration
@RequiredArgsConstructor
//웹 보안 활성화
//WebSecurityConfigurer 빈 생성
@EnableWebSecurity
public class SecurityConfig {

    private final JwtProvider jwtProvider;

    @Bean
    protected SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {
        //UI를 사용하는 것을 기본값으로 가진 시큐리티 설정을 비활성화
        httpSecurity
                .httpBasic(AbstractHttpConfigurer::disable)
                .csrf(AbstractHttpConfigurer::disable)
                .cors(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(requests ->
                        requests
                                .requestMatchers("/swagger-resources/**", "/swagger-ui/index.html", "/webjars/**", "/swagger/**", "/users/exception", "/v3/api-docs/**", "/swagger-ui/**").permitAll()
                                .requestMatchers("/users/sign-in", "/users/sign-up").permitAll()
                                .requestMatchers(HttpMethod.GET,"/users/profile", "/profile_photos/**","/snap/**","/friends/**").permitAll()
                                .requestMatchers("**exception**").permitAll()
                                .anyRequest().hasRole("USER")
                )
                .addFilterBefore(new JwtExceptionHandlerFilter(),
                        UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(new JwtAuthFilter(jwtProvider),
                        UsernamePasswordAuthenticationFilter.class)
                .exceptionHandling(exceptionHandling ->
                        exceptionHandling
                                .accessDeniedHandler(new CustomAccessDeniedHandler())
                                .authenticationEntryPoint(new CustomAuthenticationEntryPoint()));

        return httpSecurity.build();
    }

}
