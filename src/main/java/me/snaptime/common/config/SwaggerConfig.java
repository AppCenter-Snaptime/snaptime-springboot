package me.snaptime.common.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {
    @Bean
    public OpenAPI springOpenAPI() {
        return new OpenAPI()
                .info(new Info().title("Snaptime Backend Application")
                        .description("스냅타임 백엔드 애플리케이션입니다.")
                        .version("0.0.1-SNAPSHOT"));
    }
}
