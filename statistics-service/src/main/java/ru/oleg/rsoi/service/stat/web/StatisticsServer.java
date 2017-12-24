package ru.oleg.rsoi.service.stat.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import ru.oleg.rsoi.service.stat.repository.LoggedInEventRepository;
import ru.oleg.rsoi.service.stat.repository.ReservationOrderedEventRepository;

@RestController
@RequestMapping("/stat")
public class StatisticsServer {

    @Autowired
    LoggedInEventRepository loggedInEventRepository;

    @Autowired
    ReservationOrderedEventRepository reservationOrderedEventRepository;

    @RequestMapping(value = "/averageOrder", method = RequestMethod.GET)
    public int AverageOrder() {
        return reservationOrderedEventRepository.getAveragePrice();
    }
}
