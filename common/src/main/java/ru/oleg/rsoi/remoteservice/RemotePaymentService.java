package ru.oleg.rsoi.remoteservice;

import ru.oleg.rsoi.dto.BillResponse;

public interface RemotePaymentService {
    BillResponse getBill(int id);
    BillResponse createBill(int amount) throws RemoteServiceException;
    void deleteBill(int id);
}
