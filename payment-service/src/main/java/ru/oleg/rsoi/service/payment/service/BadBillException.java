package ru.oleg.rsoi.service.payment.service;

import javax.persistence.PersistenceException;

public class BadBillException extends PersistenceException {
    public BadBillException() {
        super();
    }

    public BadBillException(String message) {
        super(message);
    }
}
