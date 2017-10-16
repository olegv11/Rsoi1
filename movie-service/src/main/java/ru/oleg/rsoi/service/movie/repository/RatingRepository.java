package ru.oleg.rsoi.service.movie.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.oleg.rsoi.service.movie.domain.Movie;
import ru.oleg.rsoi.service.movie.domain.Rating;

import java.util.List;

@Repository
public interface RatingRepository extends JpaRepository<Rating, Integer>
{
    List<Rating> findAllByMovie(Movie movie);
    List<Rating> findAllByUserId(Integer user_id);
    Rating findByMovieAndUserId(Movie movie, Integer user_id);
}