package ru.oleg.rsoi.service.payment.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.oleg.rsoi.dto.BillRequest;
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

    @Autowired
    EntityManager em;

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
        if (billRequest.getAmount() == 120) {
            throw new BadBillException("Bill request has failed");
        }
        Bill bill = new Bill()
                .setAmount(billRequest.getAmount())
                .setPaymentStatus(PaymentStatus.InProcess);
        return billRepository.save(bill);
    }

    @Override
    public void delete(Integer id) {
        billRepository.delete(id);
    }
}
