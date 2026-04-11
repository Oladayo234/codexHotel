package com.semicolon.codexHotel.dtos.responses;

import com.semicolon.codexHotel.data.models.enums.ReservationStatus;
import lombok.Data;

import java.time.LocalDate;

@Data
public class ReceptionResponse {
    private String message;
    private String guestReferenceNumber;
    private String roomNumber;
    private ReservationStatus reservationStatus;
    private LocalDate checkInDate;
    private LocalDate checkOutDate;
}