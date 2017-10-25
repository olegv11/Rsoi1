package ru.oleg.rsoi.service.payment.domain;

import lombok.Data;
import lombok.experimental.Accessors;
import ru.oleg.rsoi.dto.payment.BillResponse;

import javax.persistence.*;

@Data
@Accessors(chain = true)
@Entity
@Table(name = "bill")
public class Bill {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column
    private Integer amount;

    @Enumerated(EnumType.STRING)
    private PaymentStatus paymentStatus;


    public BillResponse toResponse() {
        return new BillResponse(id, amount);
    }
}
