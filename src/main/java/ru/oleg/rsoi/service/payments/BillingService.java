package ru.oleg.rsoi.service;

import ru.oleg.rsoi.domain.payments.Bill;

public interface BillingService {
    Bill getById(Integer id);
}
