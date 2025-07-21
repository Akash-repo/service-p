package com.interview.service_p;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties // Enable it for your class
public class ServicePApplication {

	public static void main(String[] args) {
		SpringApplication.run(ServicePApplication.class, args);
	}

}
