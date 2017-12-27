package ru.oleg.rsoi.service.stat.Statistics;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.oleg.rsoi.dto.statistics.ReservationOrderedEventRequest;
import ru.oleg.rsoi.dto.statistics.VisitedMovieEvent;
import ru.oleg.rsoi.service.stat.domain.ReservationOrdered;
import ru.oleg.rsoi.service.stat.domain.VisitedMovie;
import ru.oleg.rsoi.service.stat.repository.VisitedMovieEventRepository;
import ru.oleg.rsoi.service.stat.repository.ReservationOrderedEventRepository;
import ru.oleg.rsoi.statistics.StatisticsItem;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.concurrent.Semaphore;

@Component
public class Statistics {
    private static final Logger logger = LoggerFactory.getLogger(Statistics.class);
    private ObjectMapper om = new ObjectMapper();

    private int ExpirationMilliseconds = 60000;

    private HashMap<Integer, Long> expirations = new HashMap<>();
    private HashMap<Integer, String> messages = new HashMap<>();

    private int idCounter = 1;

    @Autowired
    VisitedMovieEventRepository visitedMovieEventRepository;

    @Autowired
    ReservationOrderedEventRepository reservationOrderedEventRepository;

    @Autowired
    private RabbitTemplate template;

    private Semaphore tableSemaphore = new Semaphore(1);
    HashSet<Integer> confirmedIds = new HashSet<>();

    @RabbitListener(queues = "reservationOrderedQueue")
    public void receiveReservationOrdered(StatisticsItem in) {
        try {
            tableSemaphore.acquire();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException();
        }

        if (confirmedIds.contains(in.getId())) {
            tableSemaphore.release();
            return;
        }

        ReservationOrderedEventRequest event;
        try {
            event = om.readValue(in.data, ReservationOrderedEventRequest.class);
        }
        catch (IOException e) {
            logger.error("Error parsing message");
            error(in.getId());
            return;
        }

        if (event.getChairs() == 3) {
            error(in.getId());
            tableSemaphore.release();
            return;
        }

        ReservationOrdered ordered = new ReservationOrdered();
        ordered.setPrice(event.getPrice());
        ordered.setSeatNumber(event.getChairs());
        reservationOrderedEventRepository.save(ordered);

        success(in.getId());

        confirmedIds.add(in.getId());
        tableSemaphore.release();
    }

    @RabbitListener(queues = "visitedMovieQueue")
    public void userLoggedIn(StatisticsItem in) {
        try {
            tableSemaphore.acquire();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException();
        }

        if (confirmedIds.contains(in.getId())) {
            tableSemaphore.release();
            return;
        }

        VisitedMovieEvent event;
        try {
            event = om.readValue(in.data, VisitedMovieEvent.class);
        }
        catch (IOException e) {
            logger.error("Error parsing message");
            error(in.getId());
            return;
        }

        VisitedMovie visited = new VisitedMovie();
        visited.setMovieId(event.getMovieId());
        visited.setTime(new Date(event.getTime()));

        visitedMovieEventRepository.save(visited);
        success(in.getId());

        confirmedIds.add(in.getId());
        tableSemaphore.release();
    }

    private void success(int id) {
        template.convertAndSend("rsoi.statistics.status", "confirm", id);
    }

    private void error(int id) {
        template.convertAndSend("rsoi.statistics.status", "error", id);
    }
}
