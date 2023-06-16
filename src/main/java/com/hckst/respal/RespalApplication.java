package com.hckst.respal;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@ConfigurationPropertiesScan("com.hckst.respal.config")
@EnableAsync
public class RespalApplication {

	public static void main(String[] args) {
		SpringApplication.run(RespalApplication.class, args);
	}

}
