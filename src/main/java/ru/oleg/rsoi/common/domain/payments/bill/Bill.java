package ru.oleg.rsoi.domain.payments.bill;

import lombok.Data;
import lombok.experimental.Accessors;

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
}
