package ru.gigafood.backend.config.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Data;

@ConfigurationProperties(prefix ="app.properties")
@Data
public class AppProperties {
    private String uploadPath;
}