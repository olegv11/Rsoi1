package ru.oleg.rsoi.service.stat.Statistics;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import ru.oleg.rsoi.statistics.StatisticsItem;

import java.io.IOException;
import java.util.HashMap;
import java.util.concurrent.Semaphore;

@Component
public class Statistics {
    private static final Logger logger = LoggerFactory.getLogger(Statistics.class);
    private ObjectMapper om = new ObjectMapper();

    private int ExpirationMilliseconds = 60000;

    private HashMap<Integer, Long> expirations = new HashMap<>();
    private HashMap<Integer, String> messages = new HashMap<>();

    private Semaphore tableSemaphore = new Semaphore(1);
    private int idCounter = 1;

    @Autowired
    private RabbitTemplate template;

    @RabbitListener(queues = "reservationOrderedQueue")
    public void receiveSeanceOrdered(StatisticsItem in) {
        System.out.println("Received " + in);
        success(in.getId());
    }

    @RabbitListener(queues = "userLoggedInQueue")
    public void userLoggedIn(StatisticsItem in) {
        System.out.println("Received " + in);
        success(in.getId());
    }

    private void success(int id) {
        template.convertAndSend("rsoi.statistics.status", "confirm", id);
    }

    private void error(int id) {
        template.convertAndSend("rsoi.statistics.status", "error", id);
    }
}
