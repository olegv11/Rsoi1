package ru.oleg.rsoi.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Date;

@Data
@AllArgsConstructor
public class SeanceRequest {
    Integer movieId;
    Date screenDate;
}
