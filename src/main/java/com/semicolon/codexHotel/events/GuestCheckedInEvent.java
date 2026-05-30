package com.semicolon.codexHotel.events;

public class GuestCheckedInEvent extends HotelEvent {
    public GuestCheckedInEvent(Object source, String reservationReferenceNumber) {
        super(source, reservationReferenceNumber);
    }
}
