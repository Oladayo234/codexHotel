package com.semicolon.codexHotel.events;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;
@Getter
public class HotelEvent extends ApplicationEvent {
    private final String reservationReferenceNumber;

    public HotelEvent(Object source, String reservationReferenceNumber) {
        super(source);
        this.reservationReferenceNumber = reservationReferenceNumber;
    }

}
