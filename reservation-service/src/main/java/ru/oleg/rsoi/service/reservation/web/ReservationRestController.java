package ru.oleg.rsoi.service.reservation.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import ru.oleg.rsoi.dto.reservation.ReservationRequest;
import ru.oleg.rsoi.dto.reservation.ReservationResponse;
import ru.oleg.rsoi.service.reservation.domain.Reservation;
import ru.oleg.rsoi.service.reservation.service.ReservationService;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/reservation")
public class ReservationRestController {

    private static final Logger logger = LoggerFactory.getLogger(ExceptionController.class);

    @Autowired
    ReservationService reservationService;

    @Autowired
    ReservationRequestValidator reservationRequestValidator;

    @InitBinder("reservationRequest")
    public void setupBinder(WebDataBinder binder) {
        binder.addValidators(reservationRequestValidator);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public ReservationResponse getReservation(@PathVariable Integer id) {
        logger.debug("RESERVATION: getting reservation"+id);
        return reservationService.getById(id).toResponse();
    }

    @RequestMapping(method = RequestMethod.GET, params = "user")
    public List<ReservationResponse> getReservationByUser(@RequestParam(value = "user") Integer userId) {
        logger.debug("RESERVATION: getting reservation for user " + userId);
        return reservationService.getByUser(userId)
                .stream().map(Reservation::toResponse).collect(Collectors.toList());
    }

    @RequestMapping(method = RequestMethod.GET, params = "seance")
    public List<ReservationResponse> getReservationBySeance(@RequestParam(value = "seance") Integer seanceId) {
        logger.debug("RESERVATION: getting reservation for seance " +seanceId);
        return reservationService.getBySeance(seanceId)
                .stream().map(Reservation::toResponse).collect(Collectors.toList());
    }

    @ResponseStatus(HttpStatus.CREATED)
    @RequestMapping(method = RequestMethod.POST)
    public ReservationResponse createReservation(@Valid @RequestBody ReservationRequest reservationRequest,
                                       HttpServletResponse response) {
        logger.debug("RESERVATION: creating reservation " + reservationRequest);
        Reservation reservation = reservationService.makeReservation(reservationRequest);

        ReservationResponse r = reservation.toResponse();
        int amount = reservationService.getPriceOf(reservation);
        r.setAmount(amount);

        response.addHeader(HttpHeaders.LOCATION, "/reservation/" + reservation.getId());
        return r;
    }

    @ResponseStatus(HttpStatus.CREATED)
    @RequestMapping(value = "/{id}/bill", method = RequestMethod.POST)
    public ReservationResponse bindReservation(@PathVariable Integer id, @RequestBody Integer billId,
                                               HttpServletResponse response) {
        Reservation reservation = reservationService.bindReservationToBill(id, billId);
        response.addHeader(HttpHeaders.LOCATION, "/reservation/" + reservation.getId());
        return reservation.toResponse();
    }


    @ResponseStatus(HttpStatus.NO_CONTENT)
    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public void deleteReservation (@PathVariable Integer id) {
        logger.debug("RESERVATION: deleting reservation " + id);
        reservationService.deleteReservation(id);
    }
}
