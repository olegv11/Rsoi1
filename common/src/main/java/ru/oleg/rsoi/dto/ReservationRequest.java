package ru.oleg.rsoi.dto;

import lombok.Data;

import java.util.List;

@Data
public class ReservationRequest {
    Integer seanceId;
    Integer userId;
    List<Integer> seatIds;
}
