package com.nathannicolau.fooddeliveryapi;

import jakarta.annotation.PostConstruct;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.TimeZone;

@SpringBootApplication
public class FoodDeliveryApiApplication {

    @PostConstruct
    public void init() {
        // Garante que LocalDateTime.now() use sempre o fuso de America/Sao_Paulo
        TimeZone.setDefault(TimeZone.getTimeZone("America/Sao_Paulo"));
    }

    public static void main(String[] args) {
        SpringApplication.run(FoodDeliveryApiApplication.class, args);
    }

}
