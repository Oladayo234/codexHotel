package com.semicolon.codexHotel.utils;

import java.util.Random;

public class FrontDeskReferenceGenerator {

    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final Random RANDOM = new Random();

    public static String generateFrontDeskReference() {
        StringBuilder reference = new StringBuilder("FD-");
        for (int i = 0; i < 8; i++) {
            reference.append(CHARACTERS.charAt(RANDOM.nextInt(CHARACTERS.length())));
        }
        return reference.toString();
    }
}