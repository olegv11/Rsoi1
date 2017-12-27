package ru.oleg.rsoi.dto.statistics;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class VisitedMovieEvent {
    private int MovieId;
    private long time;
}
