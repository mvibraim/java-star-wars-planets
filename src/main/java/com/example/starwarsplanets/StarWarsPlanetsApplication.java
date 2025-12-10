package com.example.starwarsplanets;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.Contact;

@SpringBootApplication
@OpenAPIDefinition(info = @Info(title = "Star Wars Planets API", version = "1.0.0",
		description = "REST API for managing planets from the Star Wars universe",
		contact = @Contact(name = "API Support",
				url = "https://github.com/mvibraim/java-star-wars-planets")))
public class StarWarsPlanetsApplication {

	public static void main(String[] args) {
		SpringApplication.run(StarWarsPlanetsApplication.class, args);
	}
}
