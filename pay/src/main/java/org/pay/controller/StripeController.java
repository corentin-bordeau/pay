package org.pay.controller;

import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.Event;
import com.stripe.model.EventDataObjectDeserializer;
import com.stripe.model.PaymentIntent;
import com.stripe.net.Webhook;
import com.stripe.param.PaymentIntentCreateParams;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/stripe")
public class StripeController {

    @Value("${stripe.secret.key}")
    private String stripeSecretKey;

    @Value("${stripe.webhook.secret}")
    private String stripeWebhookKey;

    @PostConstruct
    public void setup() {
        Stripe.apiKey = stripeSecretKey;
    }

    /**
     * Route POST '/pay' crée une intention de paiement Stripe.
     * - Extrait 'name' du JSON reçu; renvoie erreur 400 si absent ou vide.
     * - Configure l'intention avec 200 EUR et méthode 'card', incluant 'name' en métadonnées.
     * - En succès, retourne 'clientSecret' pour finaliser paiement côté client.
     * - En échec (ex. erreur Stripe), renvoie erreur 500.
     */

    @PostMapping("/pay")
    public ResponseEntity<?> createPaymentIntent(@RequestBody Map<String, Object> data) {
        try {
            String name = (String) data.get("name");
            if (name == null || name.isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("message", "Please enter a name"));
            }

            PaymentIntentCreateParams params =
                    PaymentIntentCreateParams.builder()
                            .setAmount(200L)
                            .setCurrency("eur")
                            .addPaymentMethodType("card")
                            .putMetadata("name", name)
                            .build();

            PaymentIntent paymentIntent = PaymentIntent.create(params);

            Map<String, Object> response = new HashMap<>();
            response.put("message", "Payment initiated");
            response.put("clientSecret", paymentIntent.getClientSecret());

            return ResponseEntity.ok(response);
        } catch (StripeException e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body(Map.of("message", "Internal server error"));
        }
    }

    /**
     * Route POST '/webhook' est utilisée pour recevoir des notifications de webhook de Stripe.
     * Lorsque des événements spécifiques se produisent sur votre compte Stripe, Stripe envoie des informations
     * à cette route sous forme de requêtes POST avec un payload JSON et une signature de vérification.
     *
     * Pour tester cette route localement, vous devez:
     *
     * 1. Télécharger Stripe CLI depuis le site officiel de Stripe à l'adresse :
     *    https://stripe.com/docs/stripe-cli#install
     *
     * 2. Ajouter le chemin où se trouve 'stripe.exe' à votre variable d'environnement PATH. Ceci permettra
     *    d'utiliser la commande 'stripe' depuis n'importe quel terminal ou invite de commande.
     *
     * 3. Exécuter 'stripe login' dans le terminal pour connecter le CLI à votre compte Stripe.
     *    Cela ouvrira votre navigateur pour une authentification sécurisée.
     *
     * 4. Démarrer l'écoute des événements Stripe et les rediriger vers votre endpoint local avec la commande :
     *    'stripe listen --forward-to localhost:8080/api/v1/stripe/webhook'
     *    Stripe CLI va afficher une clé secrète de webhook que vous utiliserez pour vérifier la signature des événements reçus.
     *
     * Rappelez-vous que pour une utilisation en production, vous devez configurer votre endpoint de webhook
     * via le tableau de bord Stripe et sécuriser votre route en vérifiant la signature de chaque requête reçue.
     */
    @PostMapping("/webhook")
    public ResponseEntity<?> stripeWebhook(@RequestBody String payload, @RequestHeader("Stripe-Signature") String sigHeader) {
        try {
            String endpointSecret = stripeWebhookKey;
            Event event = Webhook.constructEvent(
                    payload, sigHeader, endpointSecret
            );

            // Gestion des types d'événement
            if ("payment_intent.created".equals(event.getType()) || "payment_intent.succeeded".equals(event.getType())) {
                EventDataObjectDeserializer dataObjectDeserializer = event.getDataObjectDeserializer();
                PaymentIntent paymentIntent = null;
                if (dataObjectDeserializer.getObject().isPresent()) {
                    paymentIntent = (PaymentIntent) dataObjectDeserializer.getObject().get();
                    String name = paymentIntent.getMetadata().get("name");
                    if ("payment_intent.created".equals(event.getType())) {
                        System.out.println(name + " initiated payment!");
                    } else {
                        System.out.println(name + " succeeded payment!");
                        // fulfilment
                    }
                }
            }

            return ResponseEntity.ok(Map.of("ok", true));
        } catch (StripeException e) {
            e.printStackTrace();
            return ResponseEntity.status(400).body(Map.of("message", e.getMessage()));
        }
    }
}
