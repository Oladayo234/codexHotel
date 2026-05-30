package com.semicolon.codexHotel;

import com.semicolon.codexHotel.config.PaystackConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@EnableConfigurationProperties
public class CodexHotelApplication {

    public static void main(String[] args) {
        SpringApplication.run(CodexHotelApplication.class, args);
    }

}
