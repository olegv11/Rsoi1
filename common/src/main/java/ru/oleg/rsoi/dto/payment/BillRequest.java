package ru.oleg.rsoi.dto.payment;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.constraints.Min;

@Data
@AllArgsConstructor
public class BillRequest {
    @Min(0)
    private Integer amount;
}
