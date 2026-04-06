package com.semicolon.codexHotel.dtos.responses;

import lombok.Data;

@Data
public class RegisterGuestResponse {
    private String message;
    private String guestId;
    private String name;
    private String email;
    private String phoneNumber;
}
