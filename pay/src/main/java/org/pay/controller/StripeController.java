package org.pay.controller;

import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.Customer;
import com.stripe.model.PaymentIntent;
import com.stripe.model.PaymentMethod;
import com.stripe.param.CustomerCreateParams;
import com.stripe.param.PaymentIntentCreateParams;
import com.stripe.param.PaymentMethodAttachParams;
import jakarta.annotation.PostConstruct;
import org.pay.model.ApiResponse;
import org.pay.model.StripePaymentRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/stripe")
public class StripeController {

    @Value("${stripe.api.secret.key}")
    private String stripeSecretKey;

    @PostConstruct
    public void init() {
        Stripe.apiKey = stripeSecretKey;
    }

    @PostMapping("/payment-intent")
    public ApiResponse<Map<String, Object>> createPaymentIntent(@RequestBody StripePaymentRequest paymentRequest) {
        Map<String, Object> response = new HashMap<>();
        try {
            // Créer un Customer
            CustomerCreateParams customerParams = CustomerCreateParams.builder().build();
            Customer customer = Customer.create(customerParams);

            // Attacher le PaymentMethod au Customer (remplacer `paymentMethodId` par la valeur réelle reçue du front-end)
            String paymentMethodId = paymentRequest.getPaymentMethodId();
            PaymentMethodAttachParams attachParams = PaymentMethodAttachParams.builder()
                    .setCustomer(customer.getId())
                    .build();
            PaymentMethod paymentMethod = PaymentMethod.retrieve(paymentMethodId);
            paymentMethod.attach(attachParams);

            // Créer un PaymentIntent avec le Customer et le PaymentMethod
            PaymentIntentCreateParams params = PaymentIntentCreateParams.builder()
                    .setAmount(paymentRequest.getAmount())
                    .setCurrency(paymentRequest.getCurrency())
                    .setCustomer(customer.getId())
                    .setPaymentMethod(paymentMethodId)
                    .setConfirmationMethod(PaymentIntentCreateParams.ConfirmationMethod.AUTOMATIC)
                    .setConfirm(true)
                    .build();

            PaymentIntent paymentIntent = PaymentIntent.create(params);
            response.put("clientSecret", paymentIntent.getClientSecret());
            return ApiResponse.success(response, "Payment intent created successfully.", HttpStatus.OK.value());
        } catch (StripeException e) {
            return ApiResponse.failure("Error creating payment intent: " + e.getMessage(), HttpStatus.BAD_REQUEST.value());
        }
    }
}
