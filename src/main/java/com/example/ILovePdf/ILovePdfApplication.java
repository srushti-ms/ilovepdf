package com.example.ILovePdf;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@EnableAsync
@SpringBootApplication
public class ILovePdfApplication {

	public static void main(String[] args) {
		SpringApplication.run(ILovePdfApplication.class, args);
	}

}
