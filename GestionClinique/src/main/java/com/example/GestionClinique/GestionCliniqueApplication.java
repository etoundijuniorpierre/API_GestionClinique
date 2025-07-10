package com.example.GestionClinique;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@SpringBootApplication
@EnableAspectJAutoProxy
public class GestionCliniqueApplication {

	public static void main(String[] args) {
		SpringApplication.run(GestionCliniqueApplication.class, args);
	}

}
