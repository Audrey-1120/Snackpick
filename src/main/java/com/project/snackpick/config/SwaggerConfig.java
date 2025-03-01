package com.project.snackpick.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Snackpick project")
                        .version("0.0.1")
                        .description("<h3>Snackpick 프로젝트 api 문서입니다.</h3>"));
    }
}
