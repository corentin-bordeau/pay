package org.pay.model;

import lombok.Data;

@Data
public class StripePaymentRequest {
    private Long amount;
    private String currency;
    private String paymentMethodId;

    public StripePaymentRequest() {}

}
