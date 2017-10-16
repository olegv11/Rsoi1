package ru.oleg.rsoi.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class MovieResponse {
    private Integer movieId;
    private String name;
    private String description;
}
