package ru.oleg.rsoi.service.stat.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.oleg.rsoi.service.stat.domain.VisitedMovie;

public interface VisitedMovieEventRepository extends JpaRepository<VisitedMovie, Integer> {
}
