package ru.oleg.rsoi.remoteservice;


import ru.oleg.rsoi.dto.ReservationResponse;
import ru.oleg.rsoi.dto.SeanceResponse;

import java.util.Date;
import java.util.List;

public interface RemoteReservationService {
    SeanceResponse getSeance(int id);
    List<SeanceResponse> getSeances(int movieId);
    SeanceResponse createSeance(int movieId, Date date);
    void deleteSeance(int id);

    ReservationResponse getReservation(int id);
    List<ReservationResponse> getReservationByUser(int userId);
    ReservationResponse createReservation(int seanceId, int userId, List<Integer> seatIds);
}
