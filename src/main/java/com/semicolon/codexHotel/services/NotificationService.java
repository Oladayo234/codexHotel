package com.semicolon.codexHotel.services;

import com.semicolon.codexHotel.data.models.Guest;
import com.semicolon.codexHotel.data.models.Reservation;
import com.semicolon.codexHotel.data.models.Room;
import com.semicolon.codexHotel.data.repositories.GuestRepository;
import com.semicolon.codexHotel.data.repositories.ReservationRepository;
import com.semicolon.codexHotel.data.repositories.RoomRepository;
import com.semicolon.codexHotel.dtos.responses.NotificationResponse;
import com.semicolon.codexHotel.exceptions.GuestNotFoundException;
import com.semicolon.codexHotel.exceptions.ReservationNotFoundException;
import com.semicolon.codexHotel.exceptions.RoomNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final ReservationRepository reservationRepository;
    private final GuestRepository guestRepository;
    private final RoomRepository roomRepository;

    public NotificationResponse sendReminder(String referenceNumber) {
        Reservation reservation = reservationRepository.findByReferenceNumber(referenceNumber)
                .orElseThrow(() -> new ReservationNotFoundException("Reservation not found"));

        Guest guest = guestRepository.findById(reservation.getGuestId())
                .orElseThrow(() -> new GuestNotFoundException("Guest not found"));

        Room room = roomRepository.findById(reservation.getRoomId())
                .orElseThrow(() -> new RoomNotFoundException("Room not found"));

        NotificationResponse response = new NotificationResponse();
        response.setMessage("Dear " + guest.getName() + ", this is a reminder for your stay at CodexHotel.");
        response.setGuestName(guest.getName());
        response.setRoomNumber(room.getRoomNumber());
        response.setCheckInDate(reservation.getCheckInDate().toString());
        return response;
    }
}