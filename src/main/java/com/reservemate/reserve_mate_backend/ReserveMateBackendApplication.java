package com.reservemate.reserve_mate_backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class ReserveMateBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(ReserveMateBackendApplication.class, args);
    }
}
