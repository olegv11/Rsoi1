package ru.oleg.rsoi.service.gateway.web;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import ru.oleg.rsoi.dto.gateway.MovieComposite;
import ru.oleg.rsoi.dto.gateway.ReservationComposite;
import ru.oleg.rsoi.dto.gateway.SeanceComposite;
import ru.oleg.rsoi.dto.movie.MovieRequest;
import ru.oleg.rsoi.dto.movie.MovieResponse;
import ru.oleg.rsoi.dto.movie.RatingRequest;
import ru.oleg.rsoi.dto.payment.BillRequest;
import ru.oleg.rsoi.dto.payment.BillResponse;
import ru.oleg.rsoi.dto.reservation.ReservationRequest;
import ru.oleg.rsoi.dto.reservation.ReservationResponse;
import ru.oleg.rsoi.dto.reservation.SeanceRequest;
import ru.oleg.rsoi.dto.reservation.SeanceResponse;
import ru.oleg.rsoi.dto.statistics.ReservationOrderedEventRequest;
import ru.oleg.rsoi.dto.statistics.StatisticsResponse;
import ru.oleg.rsoi.dto.statistics.VisitedMovieEvent;
import ru.oleg.rsoi.remoteservice.*;
import ru.oleg.rsoi.service.gateway.Queue;
import ru.oleg.rsoi.service.gateway.Statistics.Statistics;

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
    RemoteClientService clientService;

    @Autowired
    @Qualifier("retryQueue")
    Queue queue;

    @Autowired
    @Qualifier("statQueue")
    Queue statQueue;

    ObjectMapper om = new ObjectMapper();


    @Autowired
    Statistics statistics;

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

        MovieResponse movie = movieService.findMovie(movieId);

        statQueue.addTask(() -> {
            VisitedMovieEvent event = new VisitedMovieEvent();
            event.setMovieId(movieId);
            event.setTime(System.currentTimeMillis());

            String json;
            try {
                json = om.writeValueAsString(event);
            }
            catch (JsonProcessingException ex) {
                logger.error("ERROR IN JSON");
                return true;
            }

            statistics.sendMessage(json, "visitedMovie");

            return true;
        });

        return MovieComposite.from(movie);
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

        BillResponse billResponse;
        try {
             billResponse = paymentService.createBill(reservationResponse.getAmount());
        }
        catch (RemoteServiceException ex) {
            logger.info("GATEWAY: ERROR CREATING BILL. ROLLING BACK RESERVATION");
            reservationService.deleteReservation(reservationResponse.getId());
            throw ex;
        }

        reservationService.bindReservation(reservationResponse.getId(), billResponse.getBillId());
        reservationResponse.setBill_id(billResponse.getBillId());

        statQueue.addTask(() -> {
            ReservationOrderedEventRequest eventRequest = new ReservationOrderedEventRequest();
            eventRequest.setChairs(reservationResponse.getSeats().size());
            eventRequest.setPrice(reservationResponse.getAmount());
            String json;
            try {
                json = om.writeValueAsString(eventRequest);
            }
            catch (JsonProcessingException ex) {
                logger.error("ERROR IN JSON");
                return true;
            }

            statistics.sendMessage(json, "reservationOrdered");

           return true;
        });

        response.addHeader(HttpHeaders.LOCATION, "/reservation/" + reservationResponse.getId());
        return ReservationComposite.from(reservationResponse, billResponse);
    }

    @ResponseStatus(HttpStatus.CREATED)
    @RequestMapping(value ="/bill", method = RequestMethod.POST)
    public BillResponse createBill(@Valid BillRequest request, HttpServletResponse response) {
        logger.info("GATEWAY: creating bill");

        return paymentService.createBill(request.getAmount());
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
        catch (Exception ex) {
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

    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(value = "/user/authenticated", method = RequestMethod.POST)
    public ResponseEntity<Void> isAuthenticated(@RequestBody String token) {
        if (!clientService.isAuthenticated(token)) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(value = "/user/refresh", method = RequestMethod.POST)
    public ResponseEntity<TokenPair> refresh(@RequestBody String token) {
        TokenPair tokens = clientService.refreshToken(token);
        if (tokens == null) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }

        return new ResponseEntity<>(tokens, HttpStatus.OK);
    }

    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(value = "/user/isAdmin", method = RequestMethod.GET)
    public Boolean isAdmin(@RequestParam(name = "token") String token) {
        return clientService.isAdmin(token);
    }

    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(value = "/user/logout", method = RequestMethod.POST)
    public void logout(@RequestBody String token) {
        clientService.logout(token);
    }

    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(value = "/statistics", method = RequestMethod.GET)
    public StatisticsResponse stats() {
        RestTemplate rt = new RestTemplate();
        StatisticsResponse response =
                rt.getForObject("http://localhost:8094/stat/all", StatisticsResponse.class);
        return response;
    }


}
