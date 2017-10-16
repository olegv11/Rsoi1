package ru.oleg.rsoi.domain.movies.movie;

import lombok.Data;

@Data
public class MovieRequest {
    private String name;
    private String description;
}
