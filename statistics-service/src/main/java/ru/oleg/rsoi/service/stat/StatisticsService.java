package ru.oleg.rsoi.service.stat;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.web.config.EnableSpringDataWebSupport;

@SpringBootApplication(scanBasePackages = {"ru.oleg.rsoi", "ru.oleg.rsoi.serviceAuth"})
@EnableSpringDataWebSupport
public class StatisticsService {
    public static void main(String[] args) {
            SpringApplication.run(StatisticsService.class, args);
    }
}


