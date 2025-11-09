package com.maven.neuto;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class NeuApplication {
	public static void main(String[] args) {
		SpringApplication.run(NeuApplication.class, args);
		System.out.println("Learning Management System Application is running successfully!................");
	}

}
