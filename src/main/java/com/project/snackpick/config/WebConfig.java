package com.project.snackpick.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {

        registry.addResourceHandler("/images/review/**")
                .addResourceLocations("file:/home/snackpickImage/review/");

        registry.addResourceHandler("/images/profile/**")
                .addResourceLocations("file:/home/snackpickImage/profile/");
    }
}
