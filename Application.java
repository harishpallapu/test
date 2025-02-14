package com.example.healthapp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

@SpringBootApplication
public class HealthAppApplication extends SpringBootServletInitializer {

    public static void main(String[] args) {
        SpringApplication.run(HealthAppApplication.class, args);
    }
}
package com.example.healthapp.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HealthController {

    @GetMapping("/health")
    public String getHealthStatus() {
        return "Health status is good!";
    }
}
