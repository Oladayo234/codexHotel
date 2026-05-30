package com.semicolon.codexHotel.services;

import com.semicolon.codexHotel.config.PaystackConfig;
import com.semicolon.codexHotel.exceptions.PaymentException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaystackService {
    private final PaystackConfig paystackConfig;
    private final RestTemplate restTemplate;

    public String initializeTransaction(String email, double amount, String reference) {
        String url = paystackConfig.getBaseUrl() + "/transaction/initialize";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(paystackConfig.getSecretKey());

        Map<String, Object> body = new HashMap<>();
        body.put("email", email);
        body.put("amount", (int) (amount * 100)); // Paystack expects amount in kobo
        body.put("reference", reference);
        body.put("currency", "NGN");

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);

        ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.POST, entity, Map.class);

        if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
            Map<String, Object> data = (Map<String, Object>) response.getBody().get("data");
            return (String) data.get("authorization_url");
        }

        throw new PaymentException("Failed to initialize Paystack transaction");
    }

    public boolean verifyTransaction(String reference) {
        String url = paystackConfig.getBaseUrl() + "/transaction/verify/" + reference;

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(paystackConfig.getSecretKey());

        HttpEntity<Void> entity = new HttpEntity<>(headers);

        ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.GET, entity, Map.class);

        if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
            Map<String, Object> data = (Map<String, Object>) response.getBody().get("data");
            String status = (String) data.get("status");
            return "success".equals(status);
        }

        return false;
    }
}
