package ru.oleg.rsoi.remoteservice;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import ru.oleg.rsoi.dto.BillRequest;
import ru.oleg.rsoi.dto.BillResponse;


@Component
public class RemotePaymentServiceImpl implements RemotePaymentService {

    @Value("${urls.services.payments}")
    String paymentServiceUrl;

    @Override
    public BillResponse getBill(int id) {
        RestTemplate rt = new RestTemplate();
        ResponseEntity<BillResponse> response = rt.getForEntity(paymentServiceUrl + "/payment/{id}",
                BillResponse.class, id);

        if (response.getStatusCode() == HttpStatus.OK) {
            return response.getBody();
        }

        return null;
    }

    @Override
    public BillResponse createBill(int amount) throws RemoteServiceException{
        BillRequest mr = new BillRequest(amount);
        RestTemplate rt = new RestTemplate();

        ResponseEntity<BillResponse> re = rt.postForEntity(paymentServiceUrl + "/payment/", mr,
                BillResponse.class);

        if (re.getStatusCode() != HttpStatus.CREATED) {
            throw new RemoteServiceException("Movie was not created");
        }

        return re.getBody();
    }

    @Override
    public void deleteBill(int id) {
        RestTemplate rt = new RestTemplate();
        rt.delete(paymentServiceUrl + "/{id}", id);
    }
}
