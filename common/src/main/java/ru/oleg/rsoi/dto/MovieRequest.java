package ru.oleg.rsoi.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class MovieRequest {
    private String name;
    private String description;
}
