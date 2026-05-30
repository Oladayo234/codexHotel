package com.semicolon.codexHotel.events;

public class GuestCheckedOutReminderEvent extends HotelEvent {
    public GuestCheckedOutReminderEvent(Object source, String referenceNumber) {
        super(source, referenceNumber);
    }
}
