package ru.oleg.rsoi.dto.gateway;


import lombok.Data;
import lombok.experimental.Accessors;
import ru.oleg.rsoi.dto.movie.MovieResponse;

@Data
@Accessors(chain = true)
public class MovieComposite {
    private Integer movieId;
    private Double averageRating;
    private String name;
    private String description;


    static public MovieComposite from(MovieResponse response) {
        return new MovieComposite()
                .setMovieId(response.getMovieId())
                .setName(response.getName())
                .setDescription(response.getDescription())
                .setAverageRating(response.getAverageRating());
    }
}
