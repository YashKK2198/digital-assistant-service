package com.example.digitalassistant;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Digital Assistant Service - Main Application Class
 * 
 * I implemented this as the entry point for my Spring Boot REST API service.
 * This creates a service where users can create named digital assistants
 * and send messages to receive predefined responses.
 */
@SpringBootApplication
public class DigitalAssistantApplication {
    
    /**
     * Main method - starts the application and displays access URLs
     */
    public static void main(String[] args) {
        SpringApplication.run(DigitalAssistantApplication.class, args);
        
        System.out.println("=================================================");
        System.out.println("Digital Assistant Service Started Successfully!");
        System.out.println("API Base URL: http://localhost:8080/api/assistants");
        System.out.println("Frontend UI: http://localhost:8080");
        System.out.println("H2 Console: http://localhost:8080/h2-console");
        System.out.println("Health Check: http://localhost:8080/actuator/health");
        System.out.println("=================================================");
    }
}
