package com.semicolon.codexHotel.events;

public class RoomBookedEvent extends HotelEvent {
    public RoomBookedEvent(Object source, String reservationReferenceNumber) {
        super(source, reservationReferenceNumber);
    }
}
