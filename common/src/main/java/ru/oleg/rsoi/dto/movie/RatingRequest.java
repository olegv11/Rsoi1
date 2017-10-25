package ru.oleg.rsoi.dto.movie;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

@Data
@Accessors(chain = true)
@NoArgsConstructor
@AllArgsConstructor
public class RatingRequest {
    @Min(0)
    @Max(100)
    private Integer score;
    private Integer movieId;
    private Integer userId;
}
