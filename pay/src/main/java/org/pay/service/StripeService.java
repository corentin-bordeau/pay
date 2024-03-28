package org.pay.service;

import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.Customer;
import com.stripe.model.PaymentIntent;
import com.stripe.model.PaymentMethod;
import com.stripe.param.CustomerCreateParams;
import com.stripe.param.PaymentIntentCreateParams;
import com.stripe.param.PaymentMethodAttachParams;
import jakarta.annotation.PostConstruct;
import org.pay.model.StripePaymentRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class StripeService {

    @Value("${stripe.api.secret.key}")
    private String stripeSecretKey;

    @PostConstruct
    public void init() {
        Stripe.apiKey = stripeSecretKey;
    }

    public Map<String, Object> createPaymentIntent(StripePaymentRequest paymentRequest) throws StripeException {
        Map<String, Object> response = new HashMap<>();

        // Création du Customer
        CustomerCreateParams customerParams = CustomerCreateParams.builder().build();
        Customer customer = Customer.create(customerParams);

        // Attachement du PaymentMethod au Customer
        String paymentMethodId = paymentRequest.getPaymentMethodId();
        PaymentMethodAttachParams attachParams = PaymentMethodAttachParams.builder()
                .setCustomer(customer.getId())
                .build();
        PaymentMethod paymentMethod = PaymentMethod.retrieve(paymentMethodId);
        paymentMethod.attach(attachParams);

        // Création de la PaymentIntent
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

        return response;
    }
}
