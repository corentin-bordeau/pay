package org.pay.model;

import lombok.Data;

@Data
public class PaymentRequest {
    private String name;
    private Long amount;
    private String currency;
}
