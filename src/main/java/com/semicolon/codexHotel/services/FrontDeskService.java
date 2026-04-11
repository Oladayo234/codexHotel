package com.semicolon.codexHotel.services;

import com.semicolon.codexHotel.data.models.Guest;
import com.semicolon.codexHotel.data.models.Reservation;
import com.semicolon.codexHotel.data.models.Room;
import com.semicolon.codexHotel.data.models.enums.ReservationStatus;
import com.semicolon.codexHotel.data.models.enums.RoomStatus;
import com.semicolon.codexHotel.data.repositories.GuestRepository;
import com.semicolon.codexHotel.data.repositories.ReservationRepository;
import com.semicolon.codexHotel.data.repositories.RoomRepository;
import com.semicolon.codexHotel.dtos.requests.CancelReservationRequest;
import com.semicolon.codexHotel.dtos.requests.ReceptionRequest;
import com.semicolon.codexHotel.exceptions.*;
import com.semicolon.codexHotel.utils.ReservationRoomPair;
import com.semicolon.codexHotel.dtos.responses.CancelReservationResponse;
import com.semicolon.codexHotel.dtos.responses.ReceptionResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class FrontDeskService {

    private final ReservationRepository reservationRepository;
    private final RoomRepository roomRepository;
    private final GuestRepository guestRepository;

    public ReceptionResponse checkIn(ReceptionRequest request){
        ReservationRoomPair pair = findReservation(request.getReferenceNumber());
        Room room = pair.getRoom();
        Reservation reservation = pair.getReservation();

        room.setRoomStatus(RoomStatus.OCCUPIED);
        roomRepository.save(room);

        reservation.setReservationStatus(ReservationStatus.CHECKED_IN);
        reservationRepository.save(reservation);

        ReceptionResponse response = new ReceptionResponse();
        Guest guest = guestRepository.findById(reservation.getGuestId())
                .orElseThrow(() -> new GuestNotFoundException("Guest not found"));
        response.setGuestReferenceNumber(guest.getGuestReferenceNumber());
        response.setRoomNumber(room.getRoomNumber());
        response.setReservationStatus(ReservationStatus.CHECKED_IN);
        response.setCheckInDate(reservation.getCheckInDate());
        response.setCheckOutDate(reservation.getCheckOutDate());
        return response;
    }

    public ReceptionResponse checkOut(ReceptionRequest request){
        ReservationRoomPair pair = findReservation(request.getReferenceNumber());
        Room room = pair.getRoom();
        Reservation reservation = pair.getReservation();
        if (reservation.getReservationStatus() != ReservationStatus.CHECKED_IN) {
            throw new InvalidReservationStatusException("Cannot check out — guest has not checked in yet");
        }

        room.setRoomStatus(RoomStatus.AVAILABLE);
        roomRepository.save(room);

        reservation.setReservationStatus(ReservationStatus.CHECKED_OUT);
        reservationRepository.save(reservation);

        ReceptionResponse response = new ReceptionResponse();
        Guest guest = guestRepository.findById(reservation.getGuestId())
                .orElseThrow(() -> new GuestNotFoundException("Guest not found"));
        response.setGuestReferenceNumber(guest.getGuestReferenceNumber());
        response.setRoomNumber(room.getRoomNumber());
        response.setReservationStatus(ReservationStatus.CHECKED_OUT);
        response.setCheckOutDate(reservation.getCheckOutDate());
        return response;
    }

    public CancelReservationResponse cancelReservation(CancelReservationRequest request) {
        ReservationRoomPair pair = findReservation(request.getReferenceNumber());
        Room room = pair.getRoom();
        Reservation reservation = pair.getReservation();
        if (LocalDateTime.now().isAfter(reservation.getBookingDate().plusHours(24)) ||
                reservation.getCheckInDate().isBefore(LocalDate.now().plusDays(2))) {
            throw new CancellationNotAllowedException("Cancellation not allowed. Must cancel within 24 hours of booking and at least 2 days before check-in");
        }
        room.setRoomStatus(RoomStatus.AVAILABLE);
        roomRepository.save(room);
        reservation.setReservationStatus(ReservationStatus.CANCELLED);
        reservationRepository.save(reservation);

        CancelReservationResponse response = new CancelReservationResponse();
        response.setMessage("Reservation cancelled successfully");
        response.setRoomNumber(room.getRoomNumber());
        return response;
    }

    private ReservationRoomPair findReservation(String referenceNumber) {
        Reservation reservation = reservationRepository.findByReferenceNumber(referenceNumber)
                .orElseThrow(() -> new ReservationNotFoundException("Reservation not found"));
        Room room = roomRepository.findById(reservation.getRoomId())
                .orElseThrow(() -> new RoomNotFoundException("Room not found"));

        return new ReservationRoomPair(reservation, room);
    }
}
