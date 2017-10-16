package ru.oleg.rsoi.domain.reservations.seance;

import lombok.Data;

import java.util.Date;

@Data
public class SeanceRequest {
    Integer movie_id;
    Date screenDate;
}
