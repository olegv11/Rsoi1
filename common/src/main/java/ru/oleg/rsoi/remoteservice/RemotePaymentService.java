package ru.oleg.rsoi.remoteservice;

import ru.oleg.rsoi.dto.payment.BillResponse;

public interface RemotePaymentService {
    BillResponse findBill(int id);
    BillResponse createBill(int amount) throws RemoteServiceException;
    void deleteBill(int id);
}
