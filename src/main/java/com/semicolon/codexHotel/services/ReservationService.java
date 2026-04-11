package com.semicolon.codexHotel.services;

import com.semicolon.codexHotel.data.models.Guest;
import com.semicolon.codexHotel.data.models.Reservation;
import com.semicolon.codexHotel.data.models.Room;
import com.semicolon.codexHotel.data.models.enums.RoomStatus;
import com.semicolon.codexHotel.data.repositories.GuestRepository;
import com.semicolon.codexHotel.data.repositories.ReservationRepository;
import com.semicolon.codexHotel.data.repositories.RoomRepository;
import com.semicolon.codexHotel.dtos.requests.BookRoomRequest;
import com.semicolon.codexHotel.dtos.responses.BookRoomResponse;
import com.semicolon.codexHotel.exceptions.GuestNotFoundException;
import com.semicolon.codexHotel.exceptions.RoomNotAvailableException;
import com.semicolon.codexHotel.utils.PaymentCalculator;
import com.semicolon.codexHotel.utils.ReservationMapper;
import com.semicolon.codexHotel.utils.ReservationReferenceGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final RoomRepository roomRepository;
    private final GuestRepository guestRepository;

    public BookRoomResponse bookRoom(BookRoomRequest request) {
        Guest guest = guestRepository.findByGuestReferenceNumber(request.getGuestReferenceNumber())
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

        Room room = availableRooms.getFirst();

        LocalDate checkInDate = LocalDate.now();
        LocalDate checkOutDate = checkInDate.plusDays(request.getNumberOfNights());

        boolean isFestive = PaymentCalculator.isFestivePeriod(checkInDate.getMonthValue(), checkInDate.getDayOfMonth());
        double totalPayment = PaymentCalculator.calculateTotalPayment(room.getRoomType(), request.getNumberOfNights(), isFestive);

        Reservation reservation = new Reservation();
        reservation.setReferenceNumber(ReservationReferenceGenerator.generateReservationReference());
        reservation.setGuestId(guest.getId());
        reservation.setRoomId(room.getId());
        reservation.setCheckInDate(checkInDate);
        reservation.setCheckOutDate(checkOutDate);
        reservation.setNumberOfNights(request.getNumberOfNights());
        reservation.setFestivePeriod(isFestive);
        reservation.setTotalPayment(totalPayment);
        reservationRepository.save(reservation);

        room.setRoomStatus(RoomStatus.RESERVED);
        roomRepository.save(room);

        return ReservationMapper.toBookRoomResponse(reservation, guest, room);
    }

    public List<Reservation> getReservationsByGuestReferenceNumber(String guestReferenceNumber) {
        Guest guest = guestRepository.findByGuestReferenceNumber(guestReferenceNumber)
                .orElseThrow(() -> new GuestNotFoundException("Guest not found"));
        return reservationRepository.findByGuestId(guest.getId());
    }
}