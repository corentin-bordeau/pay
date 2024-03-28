package org.pay.controller;

import org.pay.model.ApiResponse;
import org.pay.model.StripePaymentRequest;
import org.pay.service.StripeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.stripe.exception.StripeException;

import java.util.Map;

/**
 * Contrôleur pour gérer les requêtes de paiement via Stripe.
 *
 * Ce contrôleur sert de point d'entrée pour les opérations de paiement, telles que
 * la création d'intentions de paiement. Il délègue la logique métier au service StripeService,
 * rendant ainsi le code plus propre et plus facile à maintenir.
 */
@RestController
@RequestMapping("/stripe")
public class StripeController {

    private final StripeService stripeService;

    @Autowired
    public StripeController(StripeService stripeService) {
        this.stripeService = stripeService;
    }

    /**
     * Crée une intention de paiement Stripe.
     *
     * Accepte une requête de paiement, valide les données d'entrée, et utilise StripeService
     * pour créer une intention de paiement. Retourne les détails nécessaires au frontend pour
     * procéder au paiement.
     *
     * @param paymentRequest Le corps de la requête contenant les détails du paiement.
     * @return ApiResponse contenant le clientSecret nécessaire au frontend pour procéder au paiement,
     *         ou un message d'erreur en cas d'échec.
     */
    @PostMapping("/payment-intent")
    public ApiResponse<Map<String, Object>> createPaymentIntent(@Validated @RequestBody StripePaymentRequest paymentRequest) {
        try {
            Map<String, Object> response = stripeService.createPaymentIntent(paymentRequest);
            return ApiResponse.success(response, "Payment intent created successfully.", HttpStatus.OK.value());
        } catch (StripeException e) {
            return ApiResponse.failure("Error creating payment intent: " + e.getMessage(), HttpStatus.BAD_REQUEST.value());
        }
    }
}
