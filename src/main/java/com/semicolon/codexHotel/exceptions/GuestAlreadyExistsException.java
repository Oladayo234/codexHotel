package com.semicolon.codexHotel.exceptions;

public class GuestAlreadyExistsException extends RuntimeException {
    public GuestAlreadyExistsException(String message) {
        super(message);
    }
}
