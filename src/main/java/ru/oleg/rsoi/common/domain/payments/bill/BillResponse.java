package ru.oleg.rsoi.domain.payments.bill;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class BillResponse {
    private Integer bill_id;
    private Integer amount;

    public BillResponse(Bill bill) {
        this.bill_id = bill.getId();
        this.amount = bill.getAmount();
    }
}
