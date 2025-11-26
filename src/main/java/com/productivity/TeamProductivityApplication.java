package com.productivity;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Main Spring Boot application class for Team Productivity Dashboard backend.
 */
@SpringBootApplication
public class TeamProductivityApplication {

    public static void main(String[] args) {

        SpringApplication.run(TeamProductivityApplication.class, args);
        System.out.println("This Works !!");
    }
}