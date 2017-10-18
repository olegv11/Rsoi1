package ru.oleg.rsoi.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class ReservationRequest {
    Integer seanceId;
    Integer userId;
    List<Integer> seatIds;
}
