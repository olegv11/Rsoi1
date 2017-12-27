package ru.oleg.rsoi.remoteservice;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import ru.oleg.rsoi.dto.gateway.MovieComposite;
import ru.oleg.rsoi.dto.gateway.ReservationComposite;
import ru.oleg.rsoi.dto.gateway.SeanceComposite;
import ru.oleg.rsoi.dto.movie.MovieRequest;
import ru.oleg.rsoi.dto.movie.RatingRequest;
import ru.oleg.rsoi.dto.payment.BillResponse;
import ru.oleg.rsoi.dto.reservation.ReservationRequest;
import ru.oleg.rsoi.dto.reservation.SeanceRequest;
import ru.oleg.rsoi.dto.statistics.StatisticsResponse;

import java.util.List;

public interface RemoteGatewayService {
    Page<MovieComposite> getMovies(Pageable pageable);
    MovieComposite getMovie(Integer id);
    MovieComposite createMovie(MovieRequest request);
    void updateMovie(Integer id, MovieRequest request);
    void deleteMovie(Integer id);
    void rateMovie(RatingRequest request);

    List<SeanceComposite> getSeances(Integer movieId);
    SeanceComposite getSeance(Integer id);
    SeanceComposite createSeance(SeanceRequest request);
    void deleteSeance(Integer id);

    ReservationComposite getReservation(Integer id);
    List<ReservationComposite> getUserReservations(Integer userId);
    ReservationComposite createReservation(ReservationRequest request);
    void deleteReservation(Integer id);

    BillResponse createBill(Integer amount);

    boolean isAuthenticated(String token);
    boolean isAdmin(String token);
    TokenPair refreshToken(String refreshToken);
    void logout(String token);
    StatisticsResponse statistics();
}
