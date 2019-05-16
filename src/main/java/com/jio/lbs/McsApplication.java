package com.jio.lbs;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Configuration;

@Configuration
@SpringBootApplication
public class McsApplication {

    public static void main(String[] args) {
        SpringApplication.run(McsApplication.class, args);
    }
}