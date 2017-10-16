package ru.oleg.rsoi.service.reservation.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.oleg.rsoi.service.reservation.domain.Seat;

public interface SeatRepository extends JpaRepository<Seat, Integer> {
}
