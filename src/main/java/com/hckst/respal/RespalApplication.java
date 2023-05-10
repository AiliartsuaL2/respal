package com.hckst.respal;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan("com.hckst.respal.oauth.properties")
public class RespalApplication {

	public static void main(String[] args) {
		SpringApplication.run(RespalApplication.class, args);
	}

}
