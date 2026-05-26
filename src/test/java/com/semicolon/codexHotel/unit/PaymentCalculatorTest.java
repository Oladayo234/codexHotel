package com.semicolon.codexHotel.unit;

import com.semicolon.codexHotel.data.models.enums.RoomType;
import com.semicolon.codexHotel.utils.PaymentCalculator;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PaymentCalculatorTest {

    // ── isFestivePeriod ──────────────────────────────────────────────────────

    @Test
    void isFestivePeriod_december15_returnsTrue() {
        assertTrue(PaymentCalculator.isFestivePeriod(12, 15));
    }

    @Test
    void isFestivePeriod_december31_returnsTrue() {
        assertTrue(PaymentCalculator.isFestivePeriod(12, 31));
    }

    @Test
    void isFestivePeriod_december14_returnsFalse() {
        assertFalse(PaymentCalculator.isFestivePeriod(12, 14));
    }

    @Test
    void isFestivePeriod_january1_returnsTrue() {
        assertTrue(PaymentCalculator.isFestivePeriod(1, 1));
    }

    @Test
    void isFestivePeriod_january5_returnsTrue() {
        assertTrue(PaymentCalculator.isFestivePeriod(1, 5));
    }

    @Test
    void isFestivePeriod_january6_returnsFalse() {
        assertFalse(PaymentCalculator.isFestivePeriod(1, 6));
    }

    @Test
    void isFestivePeriod_midYear_returnsFalse() {
        assertFalse(PaymentCalculator.isFestivePeriod(7, 4));
    }

    // ── calculateTotalPayment ────────────────────────────────────────────────

    @Test
    void calculateTotalPayment_singleRoomNonFestive3Nights() {
        // 10_000 × 3 = 30_000
        assertEquals(30_000.0, PaymentCalculator.calculateTotalPayment(RoomType.SINGLE, 3, false));
    }

    @Test
    void calculateTotalPayment_doubleRoomNonFestive2Nights() {
        // 15_000 × 2 = 30_000
        assertEquals(30_000.0, PaymentCalculator.calculateTotalPayment(RoomType.DOUBLE, 2, false));
    }

    @Test
    void calculateTotalPayment_suiteRoomNonFestive1Night() {
        // 25_000 × 1 = 25_000
        assertEquals(25_000.0, PaymentCalculator.calculateTotalPayment(RoomType.SUITE, 1, false));
    }

    @Test
    void calculateTotalPayment_singleRoomFestive_applies20PercentSurcharge() {
        // 10_000 × 1.20 × 2 = 24_000
        assertEquals(24_000.0, PaymentCalculator.calculateTotalPayment(RoomType.SINGLE, 2, true));
    }

    @Test
    void calculateTotalPayment_doubleRoomFestive_applies20PercentSurcharge() {
        // 15_000 × 1.20 × 2 = 36_000
        assertEquals(36_000.0, PaymentCalculator.calculateTotalPayment(RoomType.DOUBLE, 2, true));
    }

    @Test
    void calculateTotalPayment_suiteRoomFestive1Night() {
        // 25_000 × 1.20 × 1 = 30_000
        assertEquals(30_000.0, PaymentCalculator.calculateTotalPayment(RoomType.SUITE, 1, true));
    }
}