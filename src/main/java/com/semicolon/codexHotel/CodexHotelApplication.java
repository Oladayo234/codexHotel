package com.semicolon.codexHotel;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class CodexHotelApplication {

    public static void main(String[] args) {
        SpringApplication.run(CodexHotelApplication.class, args);
    }

}
