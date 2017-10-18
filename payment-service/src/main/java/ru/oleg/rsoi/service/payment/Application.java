package ru.oleg.rsoi.service.payment;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication(scanBasePackages = {"ru.oleg.rsoi"})
public class Application {
    public static void main(String[] args) {
            SpringApplication.run(Application.class, args);
    }
}


