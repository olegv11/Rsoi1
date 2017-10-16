package ru.oleg.rsoi.service.reservation.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.oleg.rsoi.service.reservation.domain.Reservation;

import java.util.List;

public interface ReservationRepository extends JpaRepository<Reservation, Integer> {
    List<Reservation> findAllByUserId(Integer user_id);
}
