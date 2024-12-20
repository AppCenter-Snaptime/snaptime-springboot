package me.snaptime.config;

import lombok.RequiredArgsConstructor;
import me.snaptime.exception.handler.CustomAccessDeniedHandler;
import me.snaptime.exception.handler.CustomAuthenticationEntryPoint;
import me.snaptime.exception.handler.JwtExceptionHandlerFilter;
import me.snaptime.jwt.JwtAuthFilter;
import me.snaptime.jwt.JwtProvider;
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
                                .requestMatchers("/users/sign-in", "/users/sign-up","/users/test/sign-in").permitAll()
                                .requestMatchers("/emails/send", "/emails/verify").permitAll()
                                .requestMatchers(HttpMethod.GET,"/users/profile", "/profile-photos/**","/snap/**","/friends/**").permitAll()
                                .requestMatchers("**exception**").permitAll()
                                .requestMatchers(HttpMethod.POST).hasAnyRole("USER", "ADMIN")
                                .requestMatchers(HttpMethod.PUT).hasAnyRole("USER", "ADMIN")
                                .requestMatchers(HttpMethod.PATCH).hasAnyRole("USER", "ADMIN")
                                .requestMatchers(HttpMethod.DELETE).hasAnyRole("USER", "ADMIN")
                                .requestMatchers("/**").hasAnyRole("USER", "ADMIN", "BEN")

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
