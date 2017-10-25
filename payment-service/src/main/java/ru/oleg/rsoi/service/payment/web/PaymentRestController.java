package ru.oleg.rsoi.service.payment.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.oleg.rsoi.dto.payment.BillRequest;
import ru.oleg.rsoi.dto.payment.BillResponse;
import ru.oleg.rsoi.service.payment.domain.Bill;
import ru.oleg.rsoi.service.payment.service.BillingService;

import javax.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/payment")
public class PaymentRestController {
    @Autowired
    BillingService billingService;

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    BillResponse getBill(@PathVariable Integer id) {
        return billingService.getById(id).toResponse();
    }

    @ResponseStatus(HttpStatus.CREATED)
    @RequestMapping(method = RequestMethod.POST)
    public BillResponse createBill(@RequestBody BillRequest billRequest, HttpServletResponse response) {
        Bill bill = billingService.save(billRequest);
        response.addHeader(HttpHeaders.LOCATION, "/payment/" + bill.getId());
        return bill.toResponse();
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public void deleteBill(@PathVariable Integer id) {
        billingService.delete(id);
    }


}
