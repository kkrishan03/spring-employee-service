package com.TestingApplication_Springboot.Testing.Application;

import com.TestingApplication_Springboot.Testing.Application.services.DataService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@RequiredArgsConstructor
public class TestingApplication  implements CommandLineRunner {
//	private final DataService dataService;

	@Value("${my.variable}")
	private String myVariable;

	public static void main(String[] args) {

		SpringApplication.run(TestingApplication.class, args);
		System.out.println("Springboot Application");
	}

	@Override
	public void run(String... args) throws Exception {
		System.out.println("my variable: "+myVariable);
//		System.out.println("the loaded database is:- "+ dataService.getData());
	}
}
