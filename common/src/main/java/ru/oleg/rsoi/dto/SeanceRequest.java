package ru.oleg.rsoi.dto;

import lombok.Data;

import java.util.Date;

@Data
public class SeanceRequest {
    Integer movieId;
    Date screenDate;
}
