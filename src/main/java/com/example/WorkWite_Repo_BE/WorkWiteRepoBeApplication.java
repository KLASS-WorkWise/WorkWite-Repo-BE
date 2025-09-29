package com.example.WorkWite_Repo_BE;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class WorkWiteRepoBeApplication {

	public static void main(String[] args) {
		SpringApplication.run(WorkWiteRepoBeApplication.class, args);
	}

}
