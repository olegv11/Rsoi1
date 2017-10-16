package ru.oleg.rsoi.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class BillResponse {
    private Integer billId;
    private Integer amount;
}
