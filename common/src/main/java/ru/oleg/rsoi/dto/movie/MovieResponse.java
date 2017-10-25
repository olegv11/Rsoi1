package ru.oleg.rsoi.dto.movie;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
public class MovieResponse {
    private Integer movieId;
    private String name;
    private String description;
    private double averageRating;
}
