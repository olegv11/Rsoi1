package ru.oleg.rsoi.service.gateway.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.oleg.rsoi.dto.gateway.MovieComposite;
import ru.oleg.rsoi.dto.gateway.ReservationComposite;
import ru.oleg.rsoi.dto.gateway.SeanceComposite;
import ru.oleg.rsoi.dto.movie.MovieRequest;
import ru.oleg.rsoi.dto.movie.MovieResponse;
import ru.oleg.rsoi.dto.movie.RatingRequest;
import ru.oleg.rsoi.dto.payment.BillResponse;
import ru.oleg.rsoi.dto.reservation.ReservationRequest;
import ru.oleg.rsoi.dto.reservation.ReservationResponse;
import ru.oleg.rsoi.dto.reservation.SeanceRequest;
import ru.oleg.rsoi.dto.reservation.SeanceResponse;
import ru.oleg.rsoi.remoteservice.RemoteMovieService;
import ru.oleg.rsoi.remoteservice.RemotePaymentService;
import ru.oleg.rsoi.remoteservice.RemoteReservationService;

import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/")
public class GatewayRestController {

    @Autowired
    RemoteMovieService movieService;

    @Autowired
    RemotePaymentService paymentService;

    @Autowired
    RemoteReservationService reservationService;

    @RequestMapping(value = "/movie", method = RequestMethod.GET)
    public Page<MovieComposite> getMovies(Pageable page) {
        return movieService.movies(page).map(MovieComposite::from);
    }

    @RequestMapping(value = "/movie/{movieId}", method = RequestMethod.GET)
    public MovieComposite getMovieById(@PathVariable Integer movieId) {
        return MovieComposite.from(movieService.findMovie(movieId));
    }

    @ResponseStatus(HttpStatus.CREATED)
    @RequestMapping(value = "/movie", method = RequestMethod.POST)
    public MovieComposite createMovie(@RequestBody MovieRequest request, HttpServletResponse response) {
        MovieResponse movie = movieService.createMovie(request.getName(), request.getDescription());
        response.addHeader(HttpHeaders.LOCATION, "/movie/"+movie.getMovieId());
        return MovieComposite.from(movie);
    }

    @RequestMapping(value = "/movie/{movieId}", method = RequestMethod.PATCH)
    public void updateMovie(@PathVariable Integer movieId, @RequestBody MovieRequest request) {
        movieService.updateMovie(movieId, request.getName(), request.getDescription());
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @RequestMapping(value = "/movie/{movieId}", method = RequestMethod.DELETE)
    public void deleteMovie(@PathVariable Integer movieId) {
        List<SeanceResponse> seances = reservationService.findSeancesByMovie(movieId);
        for (SeanceResponse seance : seances) {
            List<ReservationResponse> reservations = reservationService.findReservationBySeance(seance.getSeance_id());
            if (reservations != null) {
                reservations.forEach(x -> paymentService.deleteBill(x.getBill_id()));
            }

            reservationService.deleteSeance(seance.getSeance_id());
        }
        movieService.deleteMovie(movieId);
    }

    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(value = "/rate", method = RequestMethod.POST)
    public void rateMovie(@RequestBody RatingRequest request) {
        movieService.rateMovie(request.getUserId(), request.getMovieId(), request.getScore());
    }

    @RequestMapping(value = "/movie/{movieId}/seance", method = RequestMethod.GET)
    public List<SeanceComposite> getSeances(@PathVariable Integer movieId) {
        List<SeanceResponse> seances = reservationService.findSeancesByMovie(movieId);
        MovieResponse movieResponse = movieService.findMovie(movieId);

        return seances.stream().map(x -> SeanceComposite.from(x, movieResponse)).collect(Collectors.toList());
    }

    @RequestMapping(value = "/seance/{seanceId}", method = RequestMethod.GET)
    public SeanceComposite getSeanceById(@PathVariable Integer seanceId) {
        SeanceResponse seanceResponse = reservationService.findSeance(seanceId);
        MovieResponse movieResponse = movieService.findMovie(seanceResponse.getMovie_id());

        return SeanceComposite.from(seanceResponse, movieResponse);
    }

    @ResponseStatus(HttpStatus.CREATED)
    @RequestMapping(value = "/seance", method = RequestMethod.POST)
    public SeanceComposite createSeance(@RequestBody SeanceRequest seanceRequest, HttpServletResponse response) {
        SeanceResponse seanceResponse =
                reservationService.createSeance(seanceRequest.getMovieId(), seanceRequest.getScreenDate());
        MovieResponse movieResponse = movieService.findMovie(seanceResponse.getMovie_id());
        response.addHeader(HttpHeaders.LOCATION, "/seance/" + seanceResponse.getMovie_id());
        return SeanceComposite.from(seanceResponse, movieResponse);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @RequestMapping(value = "/seance/{seanceId}", method = RequestMethod.DELETE)
    public void deleteSeance(@PathVariable Integer seanceId) {
        List<ReservationResponse> reservations = reservationService.findReservationBySeance(seanceId);
        if (reservations == null) {
            return;
        }
        reservations.forEach(x -> paymentService.deleteBill(x.getBill_id()));
        reservationService.deleteSeance(seanceId);
    }

    @RequestMapping(value = "/reservation/{reservationId}")
    public ReservationComposite getReservation(@PathVariable Integer reservationId) {
        ReservationResponse reservationResponse =
                reservationService.findReservation(reservationId);
        BillResponse billResponse =
                paymentService.findBill(reservationResponse.getBill_id());

        return ReservationComposite.from(reservationResponse, billResponse);
    }

    @RequestMapping(value = "/user/{userId}/reservation")
    public List<ReservationComposite> getReservationsForUser(@PathVariable Integer userId) {
        List<ReservationResponse> reservationResponses = reservationService.getReservationByUser(userId);

        if (reservationResponses == null) {
            return new ArrayList<>();
        }

        List<ReservationComposite> result = new ArrayList<>();
        for (ReservationResponse reservation : reservationResponses) {
            BillResponse billResponse = paymentService.findBill(reservation.getBill_id());

            result.add(ReservationComposite.from(reservation, billResponse));
        }

        return result;
    }

    @ResponseStatus(HttpStatus.CREATED)
    @RequestMapping(value = "/reservation", method = RequestMethod.POST)
    public ReservationComposite createReservation(@RequestBody ReservationRequest request, HttpServletResponse response) {
        ReservationResponse reservationResponse =
                reservationService.createReservation(request.getSeanceId(), request.getUserId(), request.getSeatIds());
        BillResponse billResponse =
                paymentService.findBill(reservationResponse.getBill_id());

        response.addHeader(HttpHeaders.LOCATION, "/reservation/" + reservationResponse.getId());
        return ReservationComposite.from(reservationResponse, billResponse);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @RequestMapping(value = "/reservation/{reservationId}", method = RequestMethod.DELETE)
    public void deleteReservation(@PathVariable Integer reservationId) {
        ReservationResponse reservationResponse =
                reservationService.findReservation(reservationId);

        if (reservationResponse == null) {
            return;
        }

        paymentService.deleteBill(reservationResponse.getBill_id());
        reservationService.deleteReservation(reservationId);
    }


}
