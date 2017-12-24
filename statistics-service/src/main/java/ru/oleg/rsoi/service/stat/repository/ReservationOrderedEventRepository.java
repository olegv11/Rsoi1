package ru.oleg.rsoi.service.stat.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.oleg.rsoi.service.stat.domain.ReservationOrderedEvent;

public interface ReservationOrderedEventRepository extends JpaRepository<ReservationOrderedEvent, Integer> {

    @Query("SELECT AVG(r.price) FROM ReservationOrderedEvent r")
    int getAveragePrice();
}
