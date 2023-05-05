package com.finalproject.springboot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages={"com.finalproject"})
public class SpringBootInteriorBusinessApps {

    public static void main(String[] args) {
        SpringApplication.run(SpringBootInteriorBusinessApps.class, args);
    }
}