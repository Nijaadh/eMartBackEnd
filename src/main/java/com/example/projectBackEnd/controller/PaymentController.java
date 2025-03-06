package com.example.projectBackEnd.controller;

import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import com.stripe.param.PaymentIntentCreateParams;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@CrossOrigin(origins = "http://localhost:4200")
@RequestMapping("/api/payments")
public class PaymentController {

    @PostMapping("/create-payment-intent")
    public ResponseEntity<Map<String, Object>> createPaymentIntent(@RequestBody Map<String, Object> request) {

        Stripe.apiKey = "sk_test_51PzFfeFoTp81ofWk0GRorrtlyJovAG61mdCgfUZpexOL8TSJgDCNvuYtyM9N9q2uKAqXnZoq8e8pGsXE67cP9pPt00U5nRSxlu";
        try {
            int amount = (int) request.get("amount");
            // Prepare the payment intent creation parameters
            Map<String, Object> params = new HashMap<>();
            params.put("amount", amount);
            params.put("currency", "usd");

            PaymentIntent paymentIntent = PaymentIntent.create(params);

            Map<String, Object> response = new HashMap<>();
            response.put("clientSecret", paymentIntent.getClientSecret());

            return ResponseEntity.ok(response);

        } catch (StripeException e) {

            e.printStackTrace();
            // Return a proper error message to the frontend
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "Payment processing failed. Please try again.");

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }
}
