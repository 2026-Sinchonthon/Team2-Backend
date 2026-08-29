package org.example.team2backend.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

// Swagger UI: http://localhost:8080/swagger-ui.html
// 로그인 기능이 붙기 전까지는 인증 스킴 없이 씁니다.
@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI openAPI() {
        Info info = new Info()
                .title("Team2-Backend API")
                .version("v1")
                .description("신촌톤 Team2-Backend API 문서입니다.");

        return new OpenAPI().info(info);
    }
}
