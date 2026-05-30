package com.semicolon.codexHotel.events;

public class ReservationCancelledEvent extends HotelEvent {
    public ReservationCancelledEvent(Object source, String reservationReferenceNumber) {
        super(source, reservationReferenceNumber);
    }
}
