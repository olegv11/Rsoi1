package ru.oleg.rsoi.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;
import java.util.stream.Collectors;

@Data
@AllArgsConstructor
@Accessors(chain = true)
public class SeanceResponse {
    private Integer seance_id;
    private Integer movie_id;
    List<SeatResponse> seats;


}
