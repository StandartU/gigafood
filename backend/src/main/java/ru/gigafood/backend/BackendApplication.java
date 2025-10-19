package ru.gigafood.backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

import ru.gigafood.backend.config.RsaProperties;

@SpringBootApplication
@EnableConfigurationProperties(RsaProperties.class)
public class BackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(BackendApplication.class, args);
	}

}
