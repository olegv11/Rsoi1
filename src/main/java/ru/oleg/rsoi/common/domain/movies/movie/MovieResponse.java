package ru.oleg.rsoi.domain.movies.movie;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class MovieResponse {
    private Integer movie_id;
    private String name;
    private String description;

    public MovieResponse(Movie movie) {
        this.movie_id = movie.getId();
        this.name = movie.getName();
        this.description = movie.getDescription();
    }
}
