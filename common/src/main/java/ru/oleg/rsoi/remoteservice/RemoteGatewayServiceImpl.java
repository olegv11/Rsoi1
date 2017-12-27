package ru.oleg.rsoi.remoteservice;

import jdk.nashorn.internal.parser.Token;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
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
import ru.oleg.rsoi.dto.statistics.StatisticsResponse;
import ru.oleg.rsoi.serviceAuth.ServiceCredentials;
import ru.oleg.rsoi.serviceAuth.ServiceTokens;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Component
public class RemoteGatewayServiceImpl implements RemoteGatewayService {

    private final RemoteRsoiServiceImpl<MovieRequest, MovieComposite> movieService;
    private final RemoteRsoiServiceImpl<RatingRequest, Void> ratingService;
    private final RemoteRsoiServiceImpl<ReservationRequest, ReservationComposite> reservationService;
    private final RemoteRsoiServiceImpl<SeanceRequest, SeanceComposite> seanceService;
    private final RemoteRsoiServiceImpl<BillRequest, BillResponse> paymentService;

    private final String gatewayUrl;

    @Autowired
    ServiceCredentials myCredentials;

    @Autowired
    @Qualifier(value = "gatewayTokens")
    ServiceTokens gatewayTokens;


    @Autowired
    public RemoteGatewayServiceImpl(@Value("${urls.services.gateway}") String gatewayServiceUrl) {
        movieService = new RemoteRsoiServiceImpl<>(gatewayServiceUrl, myCredentials, gatewayTokens,
                MovieComposite.class, MovieComposite[].class);
        ratingService = new RemoteRsoiServiceImpl<>(gatewayServiceUrl, myCredentials, gatewayTokens,
                Void.class, Void[].class);
        reservationService = new RemoteRsoiServiceImpl<>(gatewayServiceUrl, myCredentials, gatewayTokens,
                ReservationComposite.class, ReservationComposite[].class);
        seanceService = new RemoteRsoiServiceImpl<>(gatewayServiceUrl, myCredentials, gatewayTokens,
                SeanceComposite.class, SeanceComposite[].class);
        paymentService = new RemoteRsoiServiceImpl<>(gatewayServiceUrl, myCredentials, gatewayTokens,
                BillResponse.class, BillResponse[].class);
        gatewayUrl = gatewayServiceUrl;
    }

    @Override
    public Page<MovieComposite> getMovies(Pageable page) {
        Page<HashMap<String, Object>> response = movieService.findAllPaged(page, "/movie");
        List<HashMap<String, Object>> responseMap = response.getContent();

        List<MovieComposite> movies = new ArrayList<>();
        for (HashMap<String, Object> map : response) {
            movies.add(new MovieComposite()
                    .setMovieId((Integer)map.get("movieId"))
                    .setDescription((String)map.get("description"))
                    .setName((String)map.get("name"))
                    .setAverageRating((Double)map.get("averageRating")));
        }

        return new PageImpl<>(movies, page, response.getTotalElements());
    }

    @Override
    public MovieComposite getMovie(Integer id) {
        return movieService.find(id, "/movie/{id}");
    }

    @Override
    public MovieComposite createMovie(MovieRequest request) {
        return movieService.create(request, "/movie");
    }

    @Override
    public void updateMovie(Integer id, MovieRequest request) {
        movieService.update(id, request, "/movie/{id}");
    }

    @Override
    public void deleteMovie(Integer id) {
        movieService.delete(id, "/movie/{id}");
    }

    @Override
    public void rateMovie(RatingRequest request) {
        ratingService.create(request, "/rate");
    }

    @Override
    public List<SeanceComposite> getSeances(Integer movieId) {
        return seanceService.findAll(movieId, "/movie/{id}/seance");
    }

    @Override
    public SeanceComposite getSeance(Integer id) {
        return seanceService.find(id, "/seance/{id}");
    }

    @Override
    public SeanceComposite createSeance(SeanceRequest request) {
        return seanceService.create(request, "/seance");
    }

    @Override
    public void deleteSeance(Integer id) {
        seanceService.delete(id, "/seance/{id}");
    }

    @Override
    public ReservationComposite getReservation(Integer id) {
        return reservationService.find(id, "/reservation/{id}");
    }

    @Override
    public List<ReservationComposite> getUserReservations(Integer userId) {
        return reservationService.findAll(userId, "/user/{id}/reservation");
    }

    @Override
    public ReservationComposite createReservation(ReservationRequest request) {
        return reservationService.create(request, "/reservation");
    }

    @Override
    public void deleteReservation(Integer id) {
        reservationService.delete(id, "/reservation/{id}");
    }

    @Override
    public BillResponse createBill(Integer amount) {
        BillRequest request = new BillRequest(amount);
        return paymentService.create(request, "/bill");
    }

    @Override
    public boolean isAuthenticated(String token) {
        RestTemplate rt = new RestTemplate();
        try {
            ResponseEntity<Void> response = rt.postForEntity(gatewayUrl+"/user/authenticated", token, Void.class);
            return true;
        } catch (RestClientException e) {
            return false;
        }
    }

    @Override
    public boolean isAdmin(String token) {
        RestTemplate rt = new RestTemplate();
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(gatewayUrl+"/user/isAdmin")
                .queryParam("token", token);

        ResponseEntity<Boolean> response;

        try {
            response = rt.getForEntity(builder.build().encode().toUri(), Boolean.class);
        }
        catch (RestClientException e) {
            return false;
        }

        return response.getBody();
    }

    @Override
    public TokenPair refreshToken(String refreshToken) {
        RestTemplate rt = new RestTemplate();
        try {
            ResponseEntity<TokenPair> response = rt.postForEntity(gatewayUrl+"/user/refresh",
                    refreshToken, TokenPair.class);

            return response.getBody();
        } catch (RestClientException e) {
            return null;
        }
    }

    @Override
    public void logout(String token) {
        RestTemplate rt = new RestTemplate();
        try {
            ResponseEntity<Void> response = rt.postForEntity(gatewayUrl+"/user/logout",
                    token, Void.class);
        } catch (RestClientException e) {
        }
    }

    @Override
    public StatisticsResponse statistics() {
        RestTemplate rt = new RestTemplate();
        ResponseEntity<StatisticsResponse> response =
                rt.getForEntity(gatewayUrl+"/statistics", StatisticsResponse.class);
        return response.getBody();
    }
}
