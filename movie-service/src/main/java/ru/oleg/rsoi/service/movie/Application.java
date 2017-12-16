package ru.oleg.rsoi.service.movie;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.web.config.EnableSpringDataWebSupport;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication(scanBasePackages = {"ru.oleg.rsoi"})
@EnableSpringDataWebSupport
public class Application {
    public static void main(String[] args) {
            SpringApplication.run(Application.class, args);
    }
}


