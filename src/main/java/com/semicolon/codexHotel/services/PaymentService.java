package com.semicolon.codexHotel.services;

import com.semicolon.codexHotel.config.PaystackConfig;
import com.semicolon.codexHotel.data.models.Reservation;
import com.semicolon.codexHotel.data.models.enums.ReservationStatus;
import com.semicolon.codexHotel.data.repositories.ReservationRepository;
import com.semicolon.codexHotel.events.ReservationConfirmedEvent;
import com.semicolon.codexHotel.exceptions.PaymentException;
import com.semicolon.codexHotel.exceptions.ReservationNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.HexFormat;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaystackConfig paystackConfig;
    private final PaystackService paystackService;
    private final ReservationRepository reservationRepository;
    private final ApplicationEventPublisher eventPublisher;

    public void processWebhook(String paystackSignature, String rawPayload, Map<String, Object> payload) {
        if (!isValidSignature(rawPayload, paystackSignature)) {
            log.warn("[WEBHOOK] Invalid signature — request rejected");
            throw new PaymentException("Invalid webhook signature");
        }

        String event = (String) payload.get("event");
        log.info("[WEBHOOK] Event received: {}", event);

        if (!"charge.success".equals(event)) {
            log.info("[WEBHOOK] Ignoring event: {}", event);
            return;
        }

        Map<String, Object> data = (Map<String, Object>) payload.get("data");
        String reference = (String) data.get("reference");

        if (!paystackService.verifyTransaction(reference)) {
            log.warn("[WEBHOOK] Transaction verification failed for reference: {}", reference);
            throw new PaymentException("Transaction verification failed");
        }

        Reservation reservation = reservationRepository.findByReferenceNumber(reference)
                .orElseThrow(() -> new ReservationNotFoundException("Reservation not found for reference: " + reference));

        reservation.setReservationStatus(ReservationStatus.CONFIRMED);
        reservationRepository.save(reservation);

        eventPublisher.publishEvent(new ReservationConfirmedEvent(this, reference));
        log.info("[WEBHOOK] Reservation {} confirmed successfully", reference);
    }

    private boolean isValidSignature(String rawPayload, String signature) {
        try {
            Mac mac = Mac.getInstance("HmacSHA512");
            SecretKeySpec secretKey = new SecretKeySpec(
                    paystackConfig.getSecretKey().getBytes(StandardCharsets.UTF_8),
                    "HmacSHA512"
            );
            mac.init(secretKey);
            byte[] hash = mac.doFinal(rawPayload.getBytes(StandardCharsets.UTF_8));
            String computed = HexFormat.of().formatHex(hash);
            return computed.equals(signature);
        } catch (Exception e) {
            log.error("[WEBHOOK] Signature verification error: {}", e.getMessage());
            return false;
        }
    }
}