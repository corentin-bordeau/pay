/**
 * Gestionnaire global d'exceptions pour l'application, capturant et traitant les exceptions de manière centralisée.
 *
 * - Traite les exceptions liées à Stripe, retournant un statut BAD_REQUEST (400) avec un message d'erreur approprié.
 * - Gère toutes les autres exceptions non capturées spécifiquement, retournant un statut INTERNAL_SERVER_ERROR (500).
 */

package org.pay.exception;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.http.HttpStatus;
import org.pay.model.ApiResponse;
import com.stripe.exception.StripeException;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(StripeException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiResponse<String> handleStripeException(StripeException e) {
        return ApiResponse.failure("Stripe error: " + e.getMessage(), HttpStatus.BAD_REQUEST.value());
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ApiResponse<String> handleGeneralException(Exception e) {
        return ApiResponse.failure("Internal server error", HttpStatus.INTERNAL_SERVER_ERROR.value());
    }
}
