package ru.oleg.rsoi.dto.statistics;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StatisticsResponse {
    private int averagePrice;
    private int averageChairs;
    private int mostWatchedMovie;
}
