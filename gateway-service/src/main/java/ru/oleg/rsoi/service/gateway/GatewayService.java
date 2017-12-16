package ru.oleg.rsoi.service.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.web.config.EnableSpringDataWebSupport;

@SpringBootApplication(scanBasePackages = {"ru.oleg.rsoi", "ru.oleg.rsoi.serviceAuth"})
@EnableSpringDataWebSupport
public class GatewayService {
    public static void main(String[] args) {
            SpringApplication.run(GatewayService.class, args);
    }
}


