package ru.oleg.rsoi.service.reservation.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.oleg.rsoi.dto.ReservationRequest;
import ru.oleg.rsoi.dto.ReservationResponse;
import ru.oleg.rsoi.service.reservation.domain.Reservation;
import ru.oleg.rsoi.service.reservation.service.ReservationService;

import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/reservation")
public class ReservationRestController {
    @Autowired
    ReservationService reservationService;

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public ReservationResponse getReservation(@PathVariable Integer id) {
        return reservationService.getById(id).toResponse();
    }

    @RequestMapping(method = RequestMethod.GET)
    public List<ReservationResponse> getReservationByUser(@RequestParam(value = "user") Integer userId) {
        return reservationService.getByUser(userId)
                .stream().map(Reservation::toResponse).collect(Collectors.toList());
    }

    @ResponseStatus(HttpStatus.CREATED)
    @RequestMapping(method = RequestMethod.POST)
    public ReservationResponse createReservation(@RequestBody ReservationRequest reservationRequest,
                                       HttpServletResponse response) {
        Reservation reservation = reservationService.save(reservationRequest);
        response.addHeader(HttpHeaders.LOCATION, "/reservation/" + reservation.getId());
        return reservation.toResponse();
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public void deleteReservation (@PathVariable Integer id) {
        reservationService.deleteReservation(id);
    }


}
