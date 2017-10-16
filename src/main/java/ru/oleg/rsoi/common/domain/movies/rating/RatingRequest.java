package ru.oleg.rsoi.domain.movies.rating;

import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

@Data
public class RatingRequest {
    @Min(0)
    @Max(100)
    private Integer score;
    private Integer movie_id;
    private Integer user_id;
}
