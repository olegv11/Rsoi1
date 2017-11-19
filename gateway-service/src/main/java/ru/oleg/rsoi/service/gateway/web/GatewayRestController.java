package ru.oleg.rsoi.service.gateway.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
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
import ru.oleg.rsoi.remoteservice.RemoteServiceException;
import ru.oleg.rsoi.service.gateway.Queue;
import ru.oleg.rsoi.service.reservation.web.ExceptionController;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/")
public class GatewayRestController {

    private static final Logger logger = LoggerFactory.getLogger(ExceptionController.class);

    @Autowired
    RemoteMovieService movieService;

    @Autowired
    RemotePaymentService paymentService;

    @Autowired
    RemoteReservationService reservationService;

    @Autowired
    Queue queue;

    @RequestMapping(value = "/movie", method = RequestMethod.GET)
    public Page<MovieComposite> getMovies(Pageable page) {
        logger.info("GATEWAY: getting page of movies:" + page);
        Page<MovieResponse> response = movieService.movies(page);
        List<MovieComposite> composites = new ArrayList<>();
        for (MovieResponse movie : response) {
            composites.add(MovieComposite.from(movie));
        }
        //List<MovieComposite> composites = response.getContent().stream().map(MovieComposite::from).collect(Collectors.toList());
        return new PageImpl<MovieComposite>(composites, page, response.getTotalElements());
    }

    @RequestMapping(value = "/movie/{movieId}", method = RequestMethod.GET)
    public MovieComposite getMovieById(@PathVariable Integer movieId) {
        logger.info("GATEWAY: getting movie " +  movieId);
        return MovieComposite.from(movieService.findMovie(movieId));
    }

    @ResponseStatus(HttpStatus.CREATED)
    @RequestMapping(value = "/movie", method = RequestMethod.POST)
    public MovieComposite createMovie(@Valid @RequestBody MovieRequest request, HttpServletResponse response) {
        logger.info("GATEWAY: creating movie " + request);
        MovieResponse movie = movieService.createMovie(request.getName(), request.getDescription());
        response.addHeader(HttpHeaders.LOCATION, "/movie/"+movie.getMovieId());
        return MovieComposite.from(movie);
    }

    @RequestMapping(value = "/movie/{movieId}", method = RequestMethod.PUT)
    public void updateMovie(@PathVariable Integer movieId, @Valid @RequestBody MovieRequest request) {
        logger.info("GATEWAY: uodating movie " + movieId);
        movieService.updateMovie(movieId, request.getName(), request.getDescription());
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @RequestMapping(value = "/movie/{movieId}", method = RequestMethod.DELETE)
    public void deleteMovie(@PathVariable Integer movieId) {
        logger.info("GATEWAY: deleting movie " + movieId);
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
    public void rateMovie(@Valid @RequestBody RatingRequest request) {
        logger.info("GATEWAY: rating movie " + request);
        movieService.rateMovie(request.getUserId(), request.getMovieId(), request.getScore());
    }

    @RequestMapping(value = "/movie/{movieId}/seance", method = RequestMethod.GET)
    public List<SeanceComposite> getSeances(@PathVariable Integer movieId) {
        logger.info("GATEWAY: getting seances of the movie " + movieId);
        List<SeanceResponse> seances = reservationService.findSeancesByMovie(movieId);
        MovieResponse movieResponse = movieService.findMovie(movieId);

        return seances.stream().map(x -> SeanceComposite.from(x, movieResponse)).collect(Collectors.toList());
    }

    @RequestMapping(value = "/seance/{seanceId}", method = RequestMethod.GET)
    public SeanceComposite getSeanceById(@PathVariable Integer seanceId) {
        logger.info("GATEWAY: getting seance " + seanceId);
        SeanceResponse seanceResponse = reservationService.findSeance(seanceId);
        logger.info("GATEWAY: got seance" + seanceResponse);
        MovieResponse movieResponse = movieService.findMovie(seanceResponse.getMovie_id());

        return SeanceComposite.from(seanceResponse, movieResponse);
    }

    @ResponseStatus(HttpStatus.CREATED)
    @RequestMapping(value = "/seance", method = RequestMethod.POST)
    public SeanceComposite createSeance(@Valid @RequestBody SeanceRequest seanceRequest, HttpServletResponse response) {
        logger.info("GATEWAY: creating seance " + seanceRequest);
        SeanceResponse seanceResponse =
                reservationService.createSeance(seanceRequest.getMovieId(), seanceRequest.getScreenDate());
        MovieResponse movieResponse = movieService.findMovie(seanceResponse.getMovie_id());
        response.addHeader(HttpHeaders.LOCATION, "/seance/" + seanceResponse.getMovie_id());
        return SeanceComposite.from(seanceResponse, movieResponse);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @RequestMapping(value = "/seance/{seanceId}", method = RequestMethod.DELETE)
    public void deleteSeance(@PathVariable Integer seanceId) {
        logger.info("GATEWAY: deleting seance " + seanceId);
        List<ReservationResponse> reservations = reservationService.findReservationBySeance(seanceId);
        if (reservations == null) {
            return;
        }
        reservations.forEach(x -> paymentService.deleteBill(x.getBill_id()));
        reservationService.deleteSeance(seanceId);
    }

    @RequestMapping(value = "/reservation/{reservationId}")
    public ReservationComposite getReservation(@PathVariable Integer reservationId) {
        logger.info("GATEWAY: getting reservation " + reservationId);
        ReservationResponse reservationResponse =
                reservationService.findReservation(reservationId);
        BillResponse billResponse = null;
        try
        {
            billResponse = paymentService.findBill(reservationResponse.getBill_id());
        }
        catch (RemoteServiceException ex) {
            logger.info("GATEWAY: Payment service exception:" + ex.getMessage());
            return ReservationComposite.from(reservationResponse);
        }

        return ReservationComposite.from(reservationResponse, billResponse);
    }

    @RequestMapping(value = "/user/{userId}/reservation")
    public List<ReservationComposite> getReservationsForUser(@PathVariable Integer userId) {
        logger.info("GATEWAY: getting reservations for user " + userId);
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
    public ReservationComposite createReservation(@Valid @RequestBody ReservationRequest request, HttpServletResponse response) {
        logger.info("GATEWAY: creating reservation " + request);
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
        logger.info("GATEWAY: deleting reservation " + reservationId);
        try {
            ReservationResponse reservationResponse =
                    reservationService.findReservation(reservationId);

            if (reservationResponse == null) {
                return;
            }

            paymentService.deleteBill(reservationResponse.getBill_id());
            reservationService.deleteReservation(reservationId);
        }
        catch (RemoteServiceException ex) {
            queue.addTask(() ->
            {
                logger.info("TRYING AGAIN");
                try {
                    ReservationResponse reservationResponse =
                            reservationService.findReservation(reservationId);

                    if (reservationResponse == null) {
                        return true;
                    }

                    paymentService.deleteBill(reservationResponse.getBill_id());
                    reservationService.deleteReservation(reservationId);
                } catch (Exception x) { return false; }
                return true;

            });
        }
    }


}
