package com.reservemate.reserve_mate_backend.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Value("${spring.app.file.base-path}")
    private String basePath;

    @Value("${spring.app.file.profile-image}")
    private String profileImagePath;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/" + profileImagePath + "**")
            .addResourceLocations("file:" + basePath + profileImagePath);
    }
}
