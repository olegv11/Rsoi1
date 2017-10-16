package ru.oleg.rsoi.service.payment.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.oleg.rsoi.service.payment.domain.Bill;
import ru.oleg.rsoi.service.payment.domain.PaymentStatus;

import java.util.List;

public interface BillRepository extends JpaRepository<Bill, Integer> {
    List<Bill> findByPaymentStatus(PaymentStatus paymentStatus);
}
