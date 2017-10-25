package ru.oleg.rsoi.remoteservice;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ru.oleg.rsoi.dto.payment.BillRequest;
import ru.oleg.rsoi.dto.payment.BillResponse;


@Component
public class RemotePaymentServiceImpl implements RemotePaymentService {

    private final RemoteRsoiServiceImpl<BillRequest, BillResponse> remoteService;


    @Autowired
    public RemotePaymentServiceImpl(@Value("${urls.services.payments}") String paymentServiceUrl) {
        remoteService = new RemoteRsoiServiceImpl<>(paymentServiceUrl, BillResponse.class, BillResponse[].class);
    }

    @Override
    public BillResponse findBill(int id) {
        return remoteService.find(id, "/payment/{id}");
    }

    @Override
    public BillResponse createBill(int amount) throws RemoteServiceException{
        BillRequest br = new BillRequest(amount);
        return remoteService.create(br, "/payment");
    }

    @Override
    public void deleteBill(int id) {
        remoteService.delete(id, "/payment");
    }
}
