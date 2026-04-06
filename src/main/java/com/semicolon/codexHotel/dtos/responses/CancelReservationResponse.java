package com.semicolon.codexHotel.dtos.responses;

import lombok.Data;

@Data
public class CancelReservationResponse {
    private String message;
    private String roomNumber;
}
