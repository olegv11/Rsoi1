package ru.oleg.rsoi.dto.payment;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class BillRequest {
    private Integer amount;
}
