package ru.oleg.rsoi.service.movie.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.oleg.rsoi.service.movie.domain.Movie;

import java.util.List;

@Repository
public interface MovieRepository extends JpaRepository<Movie, Integer>
{
    List<Movie> findAllByName(String name);
}
