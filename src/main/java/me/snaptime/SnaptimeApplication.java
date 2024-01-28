package me.snaptime;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@OpenAPIDefinition(servers =
        {@Server(url = "https://na2ru2.me:8080/", description = "Default Server URL"),
                @Server(url = "/", description = "Development Server URL")})
@SpringBootApplication
public class SnaptimeApplication {

    public static void main(String[] args) {
        SpringApplication.run(SnaptimeApplication.class, args);
    }

}
