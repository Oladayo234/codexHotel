package com.semicolon.codexHotel.data.models;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;

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
}
