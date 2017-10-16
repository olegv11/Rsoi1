package ru.oleg.rsoi.service.reservation.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.oleg.rsoi.service.reservation.domain.SeatPrice;

public interface SeatPriceRepository extends JpaRepository<SeatPrice, Integer> {
}
