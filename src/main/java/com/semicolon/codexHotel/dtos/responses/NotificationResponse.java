package com.semicolon.codexHotel.dtos.responses;

import lombok.Data;

@Data
public class NotificationResponse {
    private String message;
    private String guestName;
    private String roomNumber;
    private String checkInDate;
}
