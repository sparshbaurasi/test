package com.example.sarvesh;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@SpringBootApplication


gggg
public class SarveshApplication {
	@GetMapping("/")

	public String home(){
		return "Welcome to aws";
	}


	public static void main(String[] args) {
		SpringApplication.run(SarveshApplication.class, args);
	}

}
