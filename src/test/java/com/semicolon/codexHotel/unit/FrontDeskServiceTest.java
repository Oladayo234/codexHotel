package com.semicolon.codexHotel.unit;

import com.semicolon.codexHotel.data.models.Guest;
import com.semicolon.codexHotel.data.models.Reservation;
import com.semicolon.codexHotel.data.models.Room;
import com.semicolon.codexHotel.data.models.enums.ReservationStatus;
import com.semicolon.codexHotel.data.models.enums.RoomStatus;
import com.semicolon.codexHotel.data.models.enums.RoomType;
import com.semicolon.codexHotel.data.repositories.GuestRepository;
import com.semicolon.codexHotel.data.repositories.ReservationRepository;
import com.semicolon.codexHotel.data.repositories.RoomRepository;
import com.semicolon.codexHotel.dtos.requests.CancelReservationRequest;
import com.semicolon.codexHotel.dtos.requests.ReceptionRequest;
import com.semicolon.codexHotel.dtos.responses.CancelReservationResponse;
import com.semicolon.codexHotel.dtos.responses.ReceptionResponse;
import com.semicolon.codexHotel.exceptions.CancellationNotAllowedException;
import com.semicolon.codexHotel.exceptions.InvalidReservationStatusException;
import com.semicolon.codexHotel.exceptions.ReservationNotFoundException;
import com.semicolon.codexHotel.services.FrontDeskService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FrontDeskServiceTest {

    @Mock private ReservationRepository  reservationRepository;
    @Mock private RoomRepository         roomRepository;
    @Mock private GuestRepository        guestRepository;
    @Mock private ApplicationEventPublisher eventPublisher;

    @InjectMocks
    private FrontDeskService frontDeskService;

    // ── helpers ───────────────────────────────────────────────────────────────

    private Reservation reservation(String ref, String roomId, String guestId, ReservationStatus status) {
        Reservation r = new Reservation();
        r.setReferenceNumber(ref);
        r.setRoomId(roomId);
        r.setGuestId(guestId);
        r.setReservationStatus(status);
        r.setCheckInDate(LocalDate.now().plusDays(3));
        r.setCheckOutDate(LocalDate.now().plusDays(5));
        r.setBookingDate(LocalDateTime.now());
        return r;
    }

    private Room room(String id, String number, RoomStatus status) {
        Room r = new Room();
        r.setId(id);
        r.setRoomNumber(number);
        r.setRoomType(RoomType.SINGLE);
        r.setRoomStatus(status);
        return r;
    }

    private Guest guest(String id, String refNum) {
        Guest g = new Guest();
        g.setId(id);
        g.setGuestReferenceNumber(refNum);
        return g;
    }

    // ── checkIn ───────────────────────────────────────────────────────────────

    @Test
    void checkIn_reservedReservation_setsOccupiedAndCheckedIn() {
        Reservation res  = reservation("RES-001", "room-1", "guest-1", ReservationStatus.RESERVED);
        Room        room = room("room-1", "SN-1", RoomStatus.RESERVED);
        Guest       g    = guest("guest-1", "GST-001");

        ReceptionRequest req = new ReceptionRequest();
        req.setReferenceNumber("RES-001");

        when(reservationRepository.findByReferenceNumber("RES-001")).thenReturn(Optional.of(res));
        when(roomRepository.findById("room-1")).thenReturn(Optional.of(room));
        when(guestRepository.findById("guest-1")).thenReturn(Optional.of(g));
        when(roomRepository.save(any(Room.class))).thenAnswer(inv -> inv.getArgument(0));
        when(reservationRepository.save(any(Reservation.class))).thenAnswer(inv -> inv.getArgument(0));

        ReceptionResponse response = frontDeskService.checkIn(req);

        assertEquals(ReservationStatus.CHECKED_IN, response.getReservationStatus());
        assertEquals("SN-1", response.getRoomNumber());
        assertEquals("GST-001", response.getGuestReferenceNumber());
        verify(roomRepository).save(argThat(r -> r.getRoomStatus() == RoomStatus.OCCUPIED));
        verify(reservationRepository).save(argThat(r -> r.getReservationStatus() == ReservationStatus.CHECKED_IN));
    }

    @Test
    void checkIn_reservationNotFound_throwsReservationNotFoundException() {
        when(reservationRepository.findByReferenceNumber("RES-INVALID"))
                .thenReturn(Optional.empty());

        ReceptionRequest req = new ReceptionRequest();
        req.setReferenceNumber("RES-INVALID");

        assertThrows(ReservationNotFoundException.class, () -> frontDeskService.checkIn(req));
    }

    // ── checkOut ──────────────────────────────────────────────────────────────

    @Test
    void checkOut_checkedInGuest_setsAvailableAndCheckedOut() {
        Reservation res  = reservation("RES-001", "room-1", "guest-1", ReservationStatus.CHECKED_IN);
        Room        room = room("room-1", "SN-1", RoomStatus.OCCUPIED);
        Guest       g    = guest("guest-1", "GST-001");

        ReceptionRequest req = new ReceptionRequest();
        req.setReferenceNumber("RES-001");

        when(reservationRepository.findByReferenceNumber("RES-001")).thenReturn(Optional.of(res));
        when(roomRepository.findById("room-1")).thenReturn(Optional.of(room));
        when(guestRepository.findById("guest-1")).thenReturn(Optional.of(g));
        when(roomRepository.save(any(Room.class))).thenAnswer(inv -> inv.getArgument(0));
        when(reservationRepository.save(any(Reservation.class))).thenAnswer(inv -> inv.getArgument(0));

        ReceptionResponse response = frontDeskService.checkOut(req);

        assertEquals(ReservationStatus.CHECKED_OUT, response.getReservationStatus());
        verify(roomRepository).save(argThat(r -> r.getRoomStatus() == RoomStatus.AVAILABLE));
        verify(reservationRepository).save(argThat(r -> r.getReservationStatus() == ReservationStatus.CHECKED_OUT));
    }

    @Test
    void checkOut_notYetCheckedIn_throwsInvalidReservationStatusException() {
        Reservation res  = reservation("RES-001", "room-1", "guest-1", ReservationStatus.RESERVED);
        Room        room = room("room-1", "SN-1", RoomStatus.RESERVED);

        ReceptionRequest req = new ReceptionRequest();
        req.setReferenceNumber("RES-001");

        when(reservationRepository.findByReferenceNumber("RES-001")).thenReturn(Optional.of(res));
        when(roomRepository.findById("room-1")).thenReturn(Optional.of(room));

        assertThrows(InvalidReservationStatusException.class, () -> frontDeskService.checkOut(req));
    }

    @Test
    void checkOut_reservationNotFound_throwsReservationNotFoundException() {
        when(reservationRepository.findByReferenceNumber("RES-INVALID"))
                .thenReturn(Optional.empty());

        ReceptionRequest req = new ReceptionRequest();
        req.setReferenceNumber("RES-INVALID");

        assertThrows(ReservationNotFoundException.class, () -> frontDeskService.checkOut(req));
    }

    // ── cancelReservation ─────────────────────────────────────────────────────

    @Test
    void cancelReservation_withinWindowAndEarlyEnough_cancelledSuccessfully() {
        Reservation res = reservation("RES-001", "room-1", "guest-1", ReservationStatus.RESERVED);
        res.setBookingDate(LocalDateTime.now().minusHours(2));   // booked 2 h ago  (< 24 h window)
        res.setCheckInDate(LocalDate.now().plusDays(5));          // check-in in 5 d (≥ 2 d buffer)
        Room room = room("room-1", "SN-1", RoomStatus.RESERVED);

        CancelReservationRequest req = new CancelReservationRequest();
        req.setReferenceNumber("RES-001");

        when(reservationRepository.findByReferenceNumber("RES-001")).thenReturn(Optional.of(res));
        when(roomRepository.findById("room-1")).thenReturn(Optional.of(room));
        when(roomRepository.save(any(Room.class))).thenAnswer(inv -> inv.getArgument(0));
        when(reservationRepository.save(any(Reservation.class))).thenAnswer(inv -> inv.getArgument(0));

        CancelReservationResponse response = frontDeskService.cancelReservation(req);

        assertEquals("Reservation cancelled successfully", response.getMessage());
        assertEquals("SN-1", response.getRoomNumber());
        verify(roomRepository).save(argThat(r -> r.getRoomStatus() == RoomStatus.AVAILABLE));
        verify(reservationRepository).save(argThat(r -> r.getReservationStatus() == ReservationStatus.CANCELLED));
    }

    @Test
    void cancelReservation_after24HourWindow_throwsCancellationNotAllowedException() {
        Reservation res = reservation("RES-001", "room-1", "guest-1", ReservationStatus.RESERVED);
        res.setBookingDate(LocalDateTime.now().minusHours(25));   // booked 25 h ago (outside window)
        res.setCheckInDate(LocalDate.now().plusDays(5));
        Room room = room("room-1", "SN-1", RoomStatus.RESERVED);

        CancelReservationRequest req = new CancelReservationRequest();
        req.setReferenceNumber("RES-001");

        when(reservationRepository.findByReferenceNumber("RES-001")).thenReturn(Optional.of(res));
        when(roomRepository.findById("room-1")).thenReturn(Optional.of(room));

        assertThrows(CancellationNotAllowedException.class,
                () -> frontDeskService.cancelReservation(req));
    }

    @Test
    void cancelReservation_checkInTooSoon_throwsCancellationNotAllowedException() {
        Reservation res = reservation("RES-001", "room-1", "guest-1", ReservationStatus.RESERVED);
        res.setBookingDate(LocalDateTime.now().minusHours(1));    // still within 24 h
        res.setCheckInDate(LocalDate.now().plusDays(1));           // only 1 day away — violates 2-day buffer
        Room room = room("room-1", "SN-1", RoomStatus.RESERVED);

        CancelReservationRequest req = new CancelReservationRequest();
        req.setReferenceNumber("RES-001");

        when(reservationRepository.findByReferenceNumber("RES-001")).thenReturn(Optional.of(res));
        when(roomRepository.findById("room-1")).thenReturn(Optional.of(room));

        assertThrows(CancellationNotAllowedException.class,
                () -> frontDeskService.cancelReservation(req));
    }

    @Test
    void cancelReservation_reservationNotFound_throwsReservationNotFoundException() {
        when(reservationRepository.findByReferenceNumber("RES-INVALID"))
                .thenReturn(Optional.empty());

        CancelReservationRequest req = new CancelReservationRequest();
        req.setReferenceNumber("RES-INVALID");

        assertThrows(ReservationNotFoundException.class,
                () -> frontDeskService.cancelReservation(req));
    }
}
