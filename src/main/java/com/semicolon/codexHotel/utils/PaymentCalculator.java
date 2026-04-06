package com.semicolon.codexHotel.utils;

import com.semicolon.codexHotel.data.models.enums.RoomType;

public class PaymentCalculator {

    private static final double FESTIVE_SURCHARGE = 0.20;

    public static double calculateTotalPayment(RoomType roomType, int numberOfNights, boolean isFestivePeriod) {
        double pricePerNight = roomType.getPricePerNight();
        if (isFestivePeriod) {
            pricePerNight = pricePerNight + (pricePerNight * FESTIVE_SURCHARGE);
        }
        return pricePerNight * numberOfNights;
    }

    public static boolean isFestivePeriod(int month, int day) {
        return (month == 12 && day >= 15) || (month == 1 && day <= 5);
    }
}