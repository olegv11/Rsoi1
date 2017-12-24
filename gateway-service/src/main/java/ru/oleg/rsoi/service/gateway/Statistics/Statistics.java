package ru.oleg.rsoi.service.gateway.Statistics;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import ru.oleg.rsoi.statistics.StatisticsItem;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Semaphore;

@Component
public class Statistics {
    private static final Logger logger = LoggerFactory.getLogger(Statistics.class);
    private ObjectMapper om = new ObjectMapper();

    private final int ExpirationMilliseconds = 60000;
    private final int MaxExpirationNumber = 3;

    private HashMap<Integer, Long> expirations = new HashMap<>();
    private HashMap<Integer, Integer> expirationNumber = new HashMap<>();

    private HashMap<Integer, String> messages = new HashMap<>();
    private HashMap<Integer, String> messageRoute = new HashMap<>();

    private Semaphore tableSemaphore = new Semaphore(1);
    private int idCounter = 1;

    @Autowired
    private RabbitTemplate template;

    @RabbitListener(queues = "confirmQueue")
    public void receiveConfirm(String in) {
        Integer id;
        try {
            id = om.readValue(in, Integer.class);
        }
        catch (IOException e) {
            logger.error("Could not parse message in confirm queue:" + e.getMessage());
            return;
        }

        RemoveItem(id);
    }

    @RabbitListener(queues = "errorQueue")
    public void receiveError(String in) {
        Integer id;
        try {
            id = om.readValue(in, Integer.class);
        }
        catch (IOException e) {
            logger.error("Could not parse message in error queue:" + e.getMessage());
            return;
        }

        logger.error("Error sent by statistics server message " + id);
        RemoveItem(id);
    }

    public void sendMessage(String message, String route) {
        int id = AddItem(message, route);
        sendMessage(id, message, route);
    }

    private void sendMessage(int id, String message, String route) {
        StatisticsItem item = new StatisticsItem(id, message);
        try {
            String jsonItem = om.writeValueAsString(item);
            template.convertAndSend("rsoi.statistics.message", route, item);
        }
        catch (JsonProcessingException ex) {
            logger.error("Json error");
            return;
        }
    }

    private int AddItem(String message, String route) {
        try {
            tableSemaphore.acquire();
        }
        catch (InterruptedException interrupted) {
            Thread.currentThread().interrupt();
        }

        int id = idCounter;
        idCounter += 1;

        messages.put(id, message);
        expirations.put(id, System.currentTimeMillis());
        messageRoute.put(id, route);
        expirationNumber.put(id, 0);

        tableSemaphore.release();
        return id;
    }


    @Scheduled(fixedDelay = 30000, initialDelay = 1000)
    public void ScanForExpired() {
        try {
            tableSemaphore.acquire();
        }
        catch (InterruptedException interrupted) {
            Thread.currentThread().interrupt();
        }

        List<Integer> itemsToUpdate = new ArrayList<>();

        expirations.forEach((id, time) -> {
            if (System.currentTimeMillis() < time + ExpirationMilliseconds) {
                return;
            }

            int expiredTimes = expirationNumber.getOrDefault(id, 0);
            if (expiredTimes < MaxExpirationNumber) {
                expirationNumber.put(id, expiredTimes + 1);
                sendMessage(id, messages.get(id), messageRoute.get(id));
                itemsToUpdate.add(id);
            } else {
                RemoveItem(id);
            }

        });

        Long currentTime = System.currentTimeMillis();
        itemsToUpdate.forEach(id -> {
            expirations.put(id, currentTime);
        });

        tableSemaphore.release();
    }

    private void RemoveItem(Integer id) {
        try {
            tableSemaphore.acquire();
        }
        catch (InterruptedException interrupted) {
            Thread.currentThread().interrupt();
        }

        messages.remove(id);
        messageRoute.remove(id);
        expirations.remove(id);
        expirationNumber.remove(id);

        tableSemaphore.release();
    }
}
