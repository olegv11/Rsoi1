package ru.oleg.rsoi.service.payment.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.oleg.rsoi.dto.payment.BillRequest;
import ru.oleg.rsoi.service.payment.domain.Bill;
import ru.oleg.rsoi.service.payment.domain.PaymentStatus;
import ru.oleg.rsoi.service.payment.repository.BillRepository;

import javax.persistence.EntityManager;
import javax.persistence.EntityNotFoundException;
import java.util.List;

@Service
public class BillingServiceImpl implements BillingService {
    @Autowired
    BillRepository billRepository;

    @Override
    @Transactional(readOnly = true)
    public List<Bill> get() {
        return billRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Bill getById(Integer id) {
        Bill bill = billRepository.findOne(id);

        if (bill == null) {
            throw new EntityNotFoundException("Bill("+id+") not found.");
        }

        return bill;
    }

    @Override
    @Transactional
    public Bill save(BillRequest billRequest) {
        Bill bill = new Bill()
                .setAmount(billRequest.getAmount())
                .setPaymentStatus(PaymentStatus.Done);
        return billRepository.save(bill);
    }

    @Override
    public void delete(Integer id) {
        if (billRepository.exists(id)) {
            billRepository.delete(id);
        }
    }
}
