package com.semicolon.codexHotel.utils;

import java.util.Random;

public class ReservationReferenceGenerator {

    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final Random RANDOM = new Random();

    public static String generateReservationReference() {
        StringBuilder reference = new StringBuilder("RES-");
        for (int i = 0; i < 8; i++) {
            reference.append(CHARACTERS.charAt(RANDOM.nextInt(CHARACTERS.length())));
        }
        return reference.toString();
    }
}