package ru.oleg.rsoi.service.stat.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.oleg.rsoi.service.stat.domain.ReservationOrdered;

public interface ReservationOrderedEventRepository extends JpaRepository<ReservationOrdered, Integer> {

    @Query("SELECT AVG(r.price) FROM ReservationOrdered r")
    int getAveragePrice();

    @Query("SELECT AVG(r.seatNumber) FROM ReservationOrdered r")
    int getAllChairs();
}
