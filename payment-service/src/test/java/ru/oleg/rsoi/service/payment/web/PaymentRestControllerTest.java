package ru.oleg.rsoi.service.payment.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import ru.oleg.rsoi.dto.payment.BillRequest;
import ru.oleg.rsoi.dto.payment.BillResponse;
import ru.oleg.rsoi.dto.reservation.SeanceResponse;
import ru.oleg.rsoi.service.payment.domain.Bill;
import ru.oleg.rsoi.service.payment.domain.PaymentStatus;
import ru.oleg.rsoi.service.payment.service.BillingService;

import javax.persistence.EntityNotFoundException;

import static org.junit.Assert.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.assertj.core.api.Assertions.*;



@RunWith(SpringRunner.class)
@WebMvcTest(PaymentRestController.class)
public class PaymentRestControllerTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper mapper;

    @MockBean
    BillingService service;

    @Test
    public void getBill() throws Exception {
        // Arrange
        Bill bill = new Bill()
                .setId(10)
                .setAmount(1000)
                .setPaymentStatus(PaymentStatus.Done);

        given(service.getById(10)).willReturn(bill);

        // Act
        MvcResult result = mvc.perform(get("/payment/10").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        BillResponse response = mapper.readValue(result.getResponse().getContentAsString(),
                BillResponse.class);

        // Assert
        assertThat(bill.toResponse()).isEqualTo(response);
    }

    @Test
    public void gettingNonexistentBillThrows() throws Exception {
        // Arrange
        given(service.getById(anyInt())).willThrow(new EntityNotFoundException("Test"));

        // Act/assert
        mvc.perform(get("/payment/10").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    public void createBill() throws Exception {
        // Arrange
        Bill bill = new Bill()
                .setId(21)
                .setAmount(200)
                .setPaymentStatus(PaymentStatus.Done);
        BillRequest request = new BillRequest(200);

        given(service.save(request)).willReturn(bill);

        // Act
        MvcResult result = mvc.perform(post("/payment").contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/payment/21"))
                .andReturn();

        BillResponse response = mapper.readValue(result.getResponse().getContentAsString(),
                BillResponse.class);

        // Assert
        assertThat(response).isEqualTo(bill.toResponse());
        verify(service, times(1)).save(request);
    }

    @Test
    public void deleteBill() throws Exception {
        mvc.perform(delete("/payment/100"))
                .andExpect(status().isNoContent());
        verify(service, times(1)).delete(100);
    }

}