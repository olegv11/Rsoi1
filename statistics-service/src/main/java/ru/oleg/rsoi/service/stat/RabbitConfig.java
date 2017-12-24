package ru.oleg.rsoi.service.stat;

import org.springframework.amqp.core.*;
import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfig {
    @Bean
    Queue reservationOrderedQueue() {
        return new Queue("reservationOrderedQueue");
    }

    @Bean
    Queue userLoggedInQueue() {
        return new Queue("userLoggedInQueue");
    }

    @Bean
    public DirectExchange direct() {
        return new DirectExchange("rsoi.statistics.message");
    }


    @Bean
    public Binding seanceOrderedBinding(DirectExchange exchange, Queue reservationOrderedQueue) {
        return BindingBuilder.bind(reservationOrderedQueue)
                .to(exchange)
                .with("reservationOrdered");
    }

    @Bean
    public Binding userLoggedInBinding(DirectExchange exchange, Queue userLoggedInQueue) {
        return BindingBuilder.bind(userLoggedInQueue)
                .to(exchange)
                .with("userLoggedIn");
    }
}
