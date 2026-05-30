package com.semicolon.codexHotel.controllers;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.semicolon.codexHotel.services.PaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {
    private final PaymentService paymentService;

    @PostMapping("/webhook")
    public ResponseEntity<Void> handleWebhook(
            @RequestHeader("x-paystack-signature") String paystackSignature,
            @RequestBody String rawPayload) {
        try {
            log.info("[WEBHOOK] Received Paystack webhook");

            ObjectMapper mapper = new ObjectMapper();
            Map<String, Object> payload = mapper.readValue(rawPayload, Map.class);

            paymentService.processWebhook(paystackSignature, rawPayload, payload);
            return ResponseEntity.status(HttpStatus.OK).build();

        } catch (Exception e) {
            log.error("[WEBHOOK] Error processing webhook: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }


}
