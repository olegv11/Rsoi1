package ru.oleg.rsoi.service.payment.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import ru.oleg.rsoi.service.payment.domain.Bill;
import ru.oleg.rsoi.service.payment.domain.PaymentStatus;
import ru.oleg.rsoi.service.payment.repository.BillRepository;

import javax.persistence.EntityNotFoundException;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import static org.junit.Assert.*;


@RunWith(SpringRunner.class)
@SpringBootTest
public class BillingServiceImplTest {
    @Autowired
    BillingServiceImpl service;

    @MockBean
    BillRepository repository;

    @Test(expected = EntityNotFoundException.class)
    public void getByIdThrowsOnNonexistentBill() throws Exception {
        // Arrange
        given(repository.findOne(anyInt())).willReturn(null);

        // Act
        service.getById(10);
    }

    @Test
    public void getByIdReturnsBill() {
        // Arrange
        Bill bill = new Bill().setId(101).setAmount(55).setPaymentStatus(PaymentStatus.Done);
        given(repository.findOne(101)).willReturn(bill);

        // Act
        Bill result = service.getById(101);

        // Assert
        assertThat(result).isEqualTo(bill);
    }

}