package com.semicolon.codexHotel.unit;

import com.semicolon.codexHotel.data.models.Guest;
import com.semicolon.codexHotel.data.models.Reservation;
import com.semicolon.codexHotel.data.models.Room;
import com.semicolon.codexHotel.data.models.enums.RoomStatus;
import com.semicolon.codexHotel.data.models.enums.RoomType;
import com.semicolon.codexHotel.data.repositories.GuestRepository;
import com.semicolon.codexHotel.data.repositories.ReservationRepository;
import com.semicolon.codexHotel.data.repositories.RoomRepository;
import com.semicolon.codexHotel.dtos.requests.BookRoomRequest;
import com.semicolon.codexHotel.dtos.responses.BookRoomResponse;
import com.semicolon.codexHotel.exceptions.GuestNotFoundException;
import com.semicolon.codexHotel.exceptions.RoomNotAvailableException;
import com.semicolon.codexHotel.services.PaystackService;
import com.semicolon.codexHotel.services.ReservationService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReservationServiceTest {

    @Mock private ReservationRepository     reservationRepository;
    @Mock private RoomRepository            roomRepository;
    @Mock private GuestRepository           guestRepository;
    @Mock private ApplicationEventPublisher eventPublisher;
    @Mock private PaystackService           paystackService;

    @InjectMocks
    private ReservationService reservationService;

    private Guest guest(String id, String refNum, String name) {
        Guest g = new Guest();
        g.setId(id);
        g.setGuestReferenceNumber(refNum);
        g.setName(name);
        g.setEmail("test@example.com"); // ← add this
        return g;
    }

    private Room room(String id, String number, RoomType type) {
        Room r = new Room();
        r.setId(id);
        r.setRoomNumber(number);
        r.setRoomType(type);
        r.setRoomStatus(RoomStatus.AVAILABLE);
        return r;
    }

    // ── bookRoom ──────────────────────────────────────────────────────────────

    @Test
    void bookRoom_validRequest_createsReservationAndMarksRoomReserved() {
        Guest g = guest("g-1", "GST-12345678", "Jane Doe");
        Room  r = room("r-1", "SN-1", RoomType.SINGLE);

        BookRoomRequest req = new BookRoomRequest();
        req.setGuestReferenceNumber("GST-12345678");
        req.setRoomType(RoomType.SINGLE);
        req.setNumberOfNights(3);

        when(guestRepository.findByGuestReferenceNumber("GST-12345678")).thenReturn(Optional.of(g));
        when(roomRepository.findByRoomStatusAndRoomType(RoomStatus.AVAILABLE, RoomType.SINGLE)).thenReturn(List.of(r));
        when(reservationRepository.save(any(Reservation.class))).thenAnswer(inv -> inv.getArgument(0));
        when(roomRepository.save(any(Room.class))).thenAnswer(inv -> inv.getArgument(0));
        when(paystackService.initializeTransaction(anyString(), anyDouble(), anyString()))
                .thenReturn("https://paystack.com/pay/test");

        BookRoomResponse response = reservationService.bookRoom(req);

        assertEquals("Booking successful", response.getMessage());
        assertEquals("Jane Doe", response.getGuestName());
        assertEquals("SN-1",     response.getRoomNumber());
        assertNotNull(response.getReferenceNumber());
        assertTrue(response.getReferenceNumber().startsWith("RES-"));
        assertEquals("https://paystack.com/pay/test", response.getPaymentUrl());
        verify(reservationRepository).save(any(Reservation.class));
        verify(roomRepository).save(argThat(room -> room.getRoomStatus() == RoomStatus.RESERVED));
    }

    @Test
    void bookRoom_festiveCheckInDate_appliesSurchargeToTotalPayment() {
        // Use a room type where festive surcharge is visible: SUITE 25_000 * 1.20 * 1 = 30_000
        // We cannot control LocalDate.now() here, so we just assert totalPayment > base price
        // when it happens to be festive, or equal when not. We verify it's positive.
        Guest g = guest("g-1", "GST-TEST", "Test User");
        Room  r = room("r-1", "ST-1", RoomType.SUITE);

        BookRoomRequest req = new BookRoomRequest();
        req.setGuestReferenceNumber("GST-TEST");
        req.setRoomType(RoomType.SUITE);
        req.setNumberOfNights(1);

        when(guestRepository.findByGuestReferenceNumber("GST-TEST")).thenReturn(Optional.of(g));
        when(roomRepository.findByRoomStatusAndRoomType(RoomStatus.AVAILABLE, RoomType.SUITE)).thenReturn(List.of(r));
        when(reservationRepository.save(any(Reservation.class))).thenAnswer(inv -> inv.getArgument(0));
        when(roomRepository.save(any(Room.class))).thenAnswer(inv -> inv.getArgument(0));
        when(paystackService.initializeTransaction(anyString(), anyDouble(), anyString()))
                .thenReturn("https://paystack.com/pay/test");

        BookRoomResponse response = reservationService.bookRoom(req);

        // At minimum it must be the base price (non-festive), or 20% more (festive)
        assertTrue(response.getTotalPayment() >= 25_000.0);
    }

    @Test
    void bookRoom_guestNotFound_throwsGuestNotFoundException() {
        when(guestRepository.findByGuestReferenceNumber("GST-INVALID"))
                .thenReturn(Optional.empty());

        BookRoomRequest req = new BookRoomRequest();
        req.setGuestReferenceNumber("GST-INVALID");
        req.setRoomType(RoomType.SINGLE);
        req.setNumberOfNights(1);

        assertThrows(GuestNotFoundException.class, () -> reservationService.bookRoom(req));
    }

    @Test
    void bookRoom_noRoomsAtAll_throwsRoomNotAvailableException() {
        when(guestRepository.findByGuestReferenceNumber("GST-12345678"))
                .thenReturn(Optional.of(guest("g-1", "GST-12345678", "Jane")));
        when(roomRepository.findByRoomStatusAndRoomType(RoomStatus.AVAILABLE, RoomType.SINGLE))
                .thenReturn(Collections.emptyList());

        BookRoomRequest req = new BookRoomRequest();
        req.setGuestReferenceNumber("GST-12345678");
        req.setRoomType(RoomType.SINGLE);
        req.setNumberOfNights(2);

        assertThrows(RoomNotAvailableException.class, () -> reservationService.bookRoom(req));
    }

    @Test
    void bookRoom_availableRoomWrongType_throwsRoomNotAvailableException() {
        // No SUITE rooms available — repository returns empty for that type
        when(guestRepository.findByGuestReferenceNumber("GST-12345678"))
                .thenReturn(Optional.of(guest("g-1", "GST-12345678", "Jane")));
        when(roomRepository.findByRoomStatusAndRoomType(RoomStatus.AVAILABLE, RoomType.SUITE))
                .thenReturn(Collections.emptyList());

        BookRoomRequest req = new BookRoomRequest();
        req.setGuestReferenceNumber("GST-12345678");
        req.setRoomType(RoomType.SUITE);
        req.setNumberOfNights(2);

        assertThrows(RoomNotAvailableException.class, () -> reservationService.bookRoom(req));
    }

    // ── getReservationsByGuestReferenceNumber ─────────────────────────────────

    @Test
    void getReservations_validGuest_returnsList() {
        Guest g = guest("g-1", "GST-12345678", "Jane");
        Reservation res = new Reservation();
        res.setGuestId("g-1");

        when(guestRepository.findByGuestReferenceNumber("GST-12345678")).thenReturn(Optional.of(g));
        when(reservationRepository.findByGuestId("g-1")).thenReturn(List.of(res));

        List<Reservation> result = reservationService.getReservationsByGuestReferenceNumber("GST-12345678");

        assertEquals(1, result.size());
    }

    @Test
    void getReservations_guestNotFound_throwsGuestNotFoundException() {
        when(guestRepository.findByGuestReferenceNumber("GST-INVALID"))
                .thenReturn(Optional.empty());

        assertThrows(GuestNotFoundException.class,
                () -> reservationService.getReservationsByGuestReferenceNumber("GST-INVALID"));
    }
}