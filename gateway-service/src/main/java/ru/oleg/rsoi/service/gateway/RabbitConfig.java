package ru.oleg.rsoi.service.gateway;

import org.springframework.amqp.core.*;
import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfig {

    @Bean
    Queue confirmQueue() {
        return new Queue("confirmQueue");
    }

    @Bean
    Queue errorQueue() {
        return new Queue("errorQueue");
    }

    @Bean
    public DirectExchange direct() {
        return new DirectExchange("rsoi.statistics.status");
    }


    @Bean
    public Binding errorBinding(DirectExchange exchange, Queue errorQueue) {
        return BindingBuilder.bind(errorQueue)
                .to(exchange)
                .with("error");
    }

    @Bean
    public Binding confirmBinding(DirectExchange exchange, Queue confirmQueue) {
        return BindingBuilder.bind(confirmQueue)
                .to(exchange)
                .with("confirm");
    }
}
