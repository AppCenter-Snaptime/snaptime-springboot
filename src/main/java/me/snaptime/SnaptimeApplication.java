package me.snaptime;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@OpenAPIDefinition(servers =
        {@Server(url = "/", description = "Development Server URL")})
@SpringBootApplication
public class SnaptimeApplication {

    public static void main(String[] args) {
        SpringApplication.run(SnaptimeApplication.class, args);
    }

}
