package ru.oleg.rsoi.dto.statistics;

import lombok.Data;

@Data
public class ReservationOrderedEventRequest {
    private int chairs;
    private int price;
}
