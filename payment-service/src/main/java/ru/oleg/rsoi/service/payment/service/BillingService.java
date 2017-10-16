package ru.oleg.rsoi.service.payment.service;

import ru.oleg.rsoi.dto.BillRequest;
import ru.oleg.rsoi.service.payment.domain.Bill;

import java.util.List;

public interface BillingService {
    List<Bill> get();
    Bill getById(Integer id);
    Bill save(BillRequest billRequest);
    void delete(Integer id);

}
