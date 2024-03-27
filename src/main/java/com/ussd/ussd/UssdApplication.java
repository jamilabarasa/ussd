package com.ussd.ussd;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {"com.ussd.ussd"})
public class UssdApplication {

	public static void main(String[] args) {
		SpringApplication.run(UssdApplication.class, args);
	}

}
