package com.skoy.microservice_transaction_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.reactive.function.client.WebClient;

@SpringBootApplication(scanBasePackages = "com.skoy.microservice_transaction_service")
public class MicroserviceTransactionServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(MicroserviceTransactionServiceApplication.class, args);
	}

	@Bean
	public WebClient.Builder webClientBuilder() {
		return WebClient.builder();
	}

}
