package com.semicolon.codexHotel.data.models;

import com.semicolon.codexHotel.data.models.enums.ReservationStatus;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Document(collection = "reservations")
public class Reservation {
    @Id
    private String id;
    private String referenceNumber;
    private String guestId;
    private String roomId;
    private LocalDate checkInDate = LocalDate.now();
    private LocalDate checkOutDate;
    private int numberOfNights;
    private boolean isFestivePeriod;
    private double totalPayment;
    private LocalDateTime bookingDate = LocalDateTime.now();
    private ReservationStatus reservationStatus = ReservationStatus.RESERVED;
}
