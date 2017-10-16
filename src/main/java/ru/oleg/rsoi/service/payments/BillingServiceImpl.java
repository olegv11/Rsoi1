package ru.oleg.rsoi.service;

import org.springframework.beans.factory.annotation.Autowired;
import ru.oleg.rsoi.domain.payments.Bill;
import ru.oleg.rsoi.repository.payments.BillRepository;

import javax.persistence.EntityNotFoundException;

public class BillingServiceImpl implements BillingService {
    @Autowired
    BillRepository billRepository;

    @Override
    public Bill getById(Integer id) {
        Bill bill = billRepository.findOne(id);

        if (bill == null) {
            throw new EntityNotFoundException("Bill("+id+") not found.");
        }

        return bill;
    }
}
