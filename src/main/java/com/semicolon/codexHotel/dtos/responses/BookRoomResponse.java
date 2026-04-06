package com.semicolon.codexHotel.dtos.responses;

import lombok.Data;

import java.time.LocalDate;

@Data
public class BookRoomResponse {
    private String message;
    private String referenceNumber;
    private String guestName;
    private String roomNumber;
    private String roomType;
    private double pricePerNight;
    private double totalPayment;
    private LocalDate checkInDate;
    private LocalDate checkOutDate;
}
