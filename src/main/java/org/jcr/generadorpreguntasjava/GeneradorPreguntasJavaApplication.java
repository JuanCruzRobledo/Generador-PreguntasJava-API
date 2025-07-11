package org.jcr.generadorpreguntasjava;

import org.jcr.generadorpreguntasjava.infrastructure.persistence.data.InitData;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class GeneradorPreguntasJavaApplication {

	public static void main(String[] args) {
		SpringApplication.run(GeneradorPreguntasJavaApplication.class, args);
	}
	@Bean
	CommandLineRunner init(InitData initData) {
		return args -> {
			initData.initData();
		};
	}
}
