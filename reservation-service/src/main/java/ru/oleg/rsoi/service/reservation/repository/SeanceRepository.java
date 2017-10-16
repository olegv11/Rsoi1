package ru.oleg.rsoi.service.reservation.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.oleg.rsoi.service.reservation.domain.Seance;

import java.util.List;

public interface SeanceRepository extends JpaRepository<Seance, Integer> {
    List<Seance> findAllByMovieId(Integer movie_id);
}
