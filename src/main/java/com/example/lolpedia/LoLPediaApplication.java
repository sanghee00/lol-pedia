package com.example.lolpedia;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan(basePackages = "com.example.lolpedia.global")
public class LoLPediaApplication {

    public static void main(String[] args) {
        SpringApplication.run(LoLPediaApplication.class, args);
    }

}
