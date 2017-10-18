package ru.oleg.rsoi.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ClientResponse {
    private Integer id;
    private String name;
}
