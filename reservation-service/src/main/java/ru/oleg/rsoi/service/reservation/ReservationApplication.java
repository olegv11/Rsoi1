package ru.oleg.rsoi.service.reservation;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {"ru.oleg.rsoi"})
public class ReservationApplication {
    public static void main(String[] args) {
            SpringApplication.run(ReservationApplication.class, args);
    }
}


