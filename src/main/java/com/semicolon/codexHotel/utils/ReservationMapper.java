package com.semicolon.codexHotel.utils;

import com.semicolon.codexHotel.data.models.Guest;
import com.semicolon.codexHotel.data.models.Reservation;
import com.semicolon.codexHotel.data.models.Room;
import com.semicolon.codexHotel.dtos.responses.BookRoomResponse;

public class ReservationMapper {

    public static BookRoomResponse toBookRoomResponse(Reservation reservation, Guest guest, Room room) {
        BookRoomResponse response = new BookRoomResponse();
        response.setMessage("Booking successful");
        response.setReferenceNumber(reservation.getReferenceNumber());
        response.setGuestName(guest.getName());
        response.setRoomNumber(room.getRoomNumber());
        response.setRoomType(room.getRoomType().name());
        response.setPricePerNight(room.getRoomType().getPricePerNight());
        response.setTotalPayment(reservation.getTotalPayment());
        response.setCheckInDate(reservation.getCheckInDate());
        response.setCheckOutDate(reservation.getCheckOutDate());
        return response;
    }
}