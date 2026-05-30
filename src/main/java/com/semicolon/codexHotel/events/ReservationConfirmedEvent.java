package com.semicolon.codexHotel.events;

public class ReservationConfirmedEvent extends HotelEvent {
    public ReservationConfirmedEvent(Object source, String reservationReferenceNumber) {
        super(source, reservationReferenceNumber);
    }
}
