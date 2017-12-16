package ru.oleg.rsoi.service.mainsite;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.web.config.EnableSpringDataWebSupport;

@SpringBootApplication(scanBasePackages = {"ru.oleg.rsoi"})
@EnableSpringDataWebSupport
public class Application {
    public static void main(String[] args) {
            SpringApplication.run(Application.class, args);
    }
}


