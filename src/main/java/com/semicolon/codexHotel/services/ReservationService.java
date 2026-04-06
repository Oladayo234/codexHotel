package com.semicolon.codexHotel.services;

import com.semicolon.codexHotel.data.models.Guest;
import com.semicolon.codexHotel.data.models.Reservation;
import com.semicolon.codexHotel.data.models.Room;
import com.semicolon.codexHotel.data.models.enums.RoomStatus;
import com.semicolon.codexHotel.data.repositories.GuestRepository;
import com.semicolon.codexHotel.data.repositories.ReservationRepository;
import com.semicolon.codexHotel.data.repositories.RoomRepository;
import com.semicolon.codexHotel.dtos.requests.BookRoomRequest;
import com.semicolon.codexHotel.dtos.requests.CancelReservationRequest;
import com.semicolon.codexHotel.dtos.responses.BookRoomResponse;
import com.semicolon.codexHotel.dtos.responses.CancelReservationResponse;
import com.semicolon.codexHotel.exceptions.GuestNotFoundException;
import com.semicolon.codexHotel.exceptions.ReservationNotFoundException;
import com.semicolon.codexHotel.exceptions.RoomNotAvailableException;
import com.semicolon.codexHotel.exceptions.RoomNotFoundException;
import com.semicolon.codexHotel.utils.PaymentCalculator;
import com.semicolon.codexHotel.utils.ReservationMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final RoomRepository roomRepository;
    private final GuestRepository guestRepository;

    public BookRoomResponse bookRoom(BookRoomRequest request) {
        Guest guest = guestRepository.findById(request.getGuestId())
                .orElseThrow(() -> new GuestNotFoundException("Guest not found"));

        List<Room> allAvailableRooms = roomRepository.findByRoomStatus(RoomStatus.AVAILABLE);
        List<Room> availableRooms = new ArrayList<>();
        for (Room room : allAvailableRooms) {
            if (room.getRoomType() == request.getRoomType()) {
                availableRooms.add(room);
            }
        }

        if (availableRooms.isEmpty()) {
            throw new RoomNotAvailableException("No available rooms of type " + request.getRoomType());
        }

        Room room = availableRooms.get(0);

        LocalDate checkInDate = LocalDate.now();
        LocalDate checkOutDate = checkInDate.plusDays(request.getNumberOfNights());

        boolean isFestive = PaymentCalculator.isFestivePeriod(checkInDate.getMonthValue(), checkInDate.getDayOfMonth());
        double totalPayment = PaymentCalculator.calculateTotalPayment(room.getRoomType(), request.getNumberOfNights(), isFestive);

        Reservation reservation = new Reservation();
        reservation.setReferenceNumber("RES" + UUID.randomUUID().toString().substring(0, 4).toUpperCase());
        reservation.setGuestId(guest.getId());
        reservation.setRoomId(room.getId());
        reservation.setCheckInDate(checkInDate);
        reservation.setCheckOutDate(checkOutDate);
        reservation.setNumberOfNights(request.getNumberOfNights());
        reservation.setFestivePeriod(isFestive);
        reservation.setTotalPayment(totalPayment);
        reservationRepository.save(reservation);

        room.setRoomStatus(RoomStatus.OCCUPIED);
        roomRepository.save(room);

        return ReservationMapper.toBookRoomResponse(reservation, guest, room);
    }

    public CancelReservationResponse cancelReservation(CancelReservationRequest request) {
        Reservation reservation = reservationRepository.findByReferenceNumber(request.getReferenceNumber())
                .orElseThrow(() -> new ReservationNotFoundException("Reservation not found"));

        Room room = roomRepository.findById(reservation.getRoomId())
                .orElseThrow(() -> new RoomNotFoundException("Room not found"));

        room.setRoomStatus(RoomStatus.AVAILABLE);
        roomRepository.save(room);
        reservationRepository.delete(reservation);

        CancelReservationResponse response = new CancelReservationResponse();
        response.setMessage("Reservation cancelled successfully");
        response.setRoomNumber(room.getRoomNumber());
        return response;
    }

    public List<Reservation> getReservationsByGuestId(String guestId) {
        return reservationRepository.findByGuestId(guestId);
    }
}