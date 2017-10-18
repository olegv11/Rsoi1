package ru.oleg.rsoi.service.reservation.service;

import ru.oleg.rsoi.dto.ReservationRequest;
import ru.oleg.rsoi.service.reservation.domain.Reservation;

import java.util.List;

public interface ReservationService {
    Reservation getById(Integer id);
    List<Reservation> getByUser(Integer user_id);
    Reservation save(ReservationRequest request);
    void deleteReservation(Integer id);
}