package com.semicolon.codexHotel.events;

public class GuestCheckedOutEvent extends HotelEvent {
    public GuestCheckedOutEvent(Object source, String reservationReferenceNumber) {
        super(source, reservationReferenceNumber);
    }
}
