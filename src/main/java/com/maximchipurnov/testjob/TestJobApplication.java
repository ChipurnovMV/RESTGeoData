package com.maximchipurnov.testjob;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class TestJobApplication {

    public static void main(String[] args) {
        SpringApplication.run(TestJobApplication.class, args);
    }

}
