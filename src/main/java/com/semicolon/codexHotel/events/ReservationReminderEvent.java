package com.semicolon.codexHotel.events;

public class ReservationReminderEvent extends HotelEvent {
    public ReservationReminderEvent(Object source, String reservationReferenceNumber) {
        super(source, reservationReferenceNumber);
    }
}
