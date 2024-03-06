package me.snaptime.common.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {
    @Bean
    public OpenAPI springOpenAPI() {
        return new OpenAPI()
                //보안 요구사항 추가, Bearer 토큰을 사용하는 인증을 요구
                .addSecurityItem(new SecurityRequirement().addList("Bearer Authentication"))
                //보안 스키마 추가, Bearer 토큰을 사용하는 API 키 스키마를 생성
                .components(new Components().addSecuritySchemes("Bearer Authentication",createAPIKeyScheme()))
                .info(new Info().title("Snaptime Backend Application")
                        .description("스냅타임 백엔드 애플리케이션입니다.")
                        .version("0.0.1-SNAPSHOT"));
    }

    //Bearer 토큰을 사용하는 API 키 스키마를 생성하고 설정
    private SecurityScheme createAPIKeyScheme(){
        //SecurityScheme 객체를 생성하고 구성, Bearer 토큰을 사용하는 HTTP 스키마로 설정
        return new SecurityScheme().type(SecurityScheme.Type.HTTP)
                //토큰의 형식을 설정, JWT(JSON Web Token)를 사용하도록 설정
                .bearerFormat("JWT")
                //스키마의 이름을 설정, "bearer"로 설정
                .scheme("bearer")
                //in() 메서드로 토큰의 전송 위치를 설정,  HTTP 요청 헤더에 설정
                //name 메서드로 토큰의 이름을 설정, "Authorization"으로 설정
                .in(SecurityScheme.In.HEADER).name("Authorization");
    }
}
