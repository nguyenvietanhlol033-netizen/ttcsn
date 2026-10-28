package com.melia.hotel;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class MeliaHotelApplication {
    public static void main(String[] args) {
        SpringApplication.run(MeliaHotelApplication.class, args);
    }
}