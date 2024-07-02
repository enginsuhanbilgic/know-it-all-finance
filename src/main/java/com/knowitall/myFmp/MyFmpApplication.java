package com.knowitall.myFmp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@RestController
public class MyFmpApplication {

	public static void main(String[] args) {
		SpringApplication.run(MyFmpApplication.class, args);
	}

	@GetMapping("/root")
	public String apiRoot(){
		return "Hello World";
	}

}
