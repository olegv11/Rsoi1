package ru.oleg.rsoi.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.oleg.rsoi.domain.movies.Movie;

@Repository
public interface MovieRepository extends JpaRepository<Movie, Integer>
{}
