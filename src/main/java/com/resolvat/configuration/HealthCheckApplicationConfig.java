package com.resolvat.configuration;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Created by korteke on 11/03/17.
 */
@EnableScheduling
@Configuration
@ComponentScan(basePackages = {"com.resolvat", "com.resolvat.model", "com.resolvat.controller"})
public class HealthCheckApplicationConfig {

}
