package com.example.projectBackEnd;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@OpenAPIDefinition(info = @Info(title = "GiftSender API",version = "2.0",description = "test"))
@EnableAsync
public class ProjectBackEndApplication {

	public static void main(String[] args) {
		SpringApplication.run(ProjectBackEndApplication.class, args);
	}

}
