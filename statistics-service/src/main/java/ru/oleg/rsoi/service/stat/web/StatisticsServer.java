package ru.oleg.rsoi.service.stat.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import ru.oleg.rsoi.dto.statistics.StatisticsResponse;
import ru.oleg.rsoi.service.stat.domain.VisitedMovie;
import ru.oleg.rsoi.service.stat.repository.VisitedMovieEventRepository;
import ru.oleg.rsoi.service.stat.repository.ReservationOrderedEventRepository;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/stat")
public class StatisticsServer {

    @Autowired
    VisitedMovieEventRepository visitedMovieEventRepository;

    @Autowired
    ReservationOrderedEventRepository reservationOrderedEventRepository;

    @RequestMapping(value = "/all", method = RequestMethod.GET)
    public StatisticsResponse all() {
        StatisticsResponse response = new StatisticsResponse();
        response.setAveragePrice(reservationOrderedEventRepository.getAveragePrice());
        response.setAverageChairs(reservationOrderedEventRepository.getAllChairs());

        List<VisitedMovie> visitedMovies =
                visitedMovieEventRepository.findAll();

        int mostWatchedMovie =
                visitedMovies.stream().collect(Collectors.groupingBy(VisitedMovie::getMovieId))
                        .entrySet().stream().max(Comparator.comparingInt(a -> a.getValue().size())).get().getKey();

        response.setMostWatchedMovie(mostWatchedMovie);

        return response;
    }

}
