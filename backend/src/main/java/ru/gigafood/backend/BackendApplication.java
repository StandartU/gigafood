package ru.gigafood.backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

import ru.gigafood.backend.config.properties.AppProperties;
import ru.gigafood.backend.config.properties.RsaProperties;

@SpringBootApplication
@EnableConfigurationProperties({
	RsaProperties.class,
	AppProperties.class
})
public class BackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(BackendApplication.class, args);
	}

}
