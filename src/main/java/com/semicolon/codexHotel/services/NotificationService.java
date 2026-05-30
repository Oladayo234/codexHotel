package com.semicolon.codexHotel.services;

import com.semicolon.codexHotel.data.models.Guest;
import com.semicolon.codexHotel.data.models.Reservation;
import com.semicolon.codexHotel.data.models.Room;
import com.semicolon.codexHotel.data.repositories.GuestRepository;
import com.semicolon.codexHotel.data.repositories.ReservationRepository;
import com.semicolon.codexHotel.data.repositories.RoomRepository;
import com.semicolon.codexHotel.events.*;
import com.semicolon.codexHotel.exceptions.GuestNotFoundException;
import com.semicolon.codexHotel.exceptions.ReservationNotFoundException;
import com.semicolon.codexHotel.exceptions.RoomNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationService {

    private final ReservationRepository reservationRepository;
    private final GuestRepository guestRepository;
    private final RoomRepository roomRepository;

    @EventListener
    public void onRoomBooked(RoomBookedEvent event) {
        NotificationContext ctx = buildContext(event.getReservationReferenceNumber());
        String message = "Dear " + ctx.guest().getName() + ", your booking is confirmed! " +
                "Room " + ctx.room().getRoomNumber() + " is reserved for you. " +
                "Check-in: " + ctx.reservation().getCheckInDate() + ". " +
                "Total: ₦" + ctx.reservation().getTotalPayment() + ". " +
                "Reference: " + ctx.reservation().getReferenceNumber();
        log.info("[NOTIFICATION - BOOKING] To: {} | {}", ctx.guest().getEmail(), message);
    }

    @EventListener
    public void onGuestCheckedIn(GuestCheckedInEvent event) {
        NotificationContext ctx = buildContext(event.getReservationReferenceNumber());
        String message = "Welcome, " + ctx.guest().getName() + "! " +
                "You have successfully checked in to Room " + ctx.room().getRoomNumber() + ". " +
                "Check-out date: " + ctx.reservation().getCheckOutDate() + ". Enjoy your stay!";
        log.info("[NOTIFICATION - CHECK-IN] To: {} | {}", ctx.guest().getEmail(), message);
    }

    @EventListener
    public void onGuestCheckedOut(GuestCheckedOutEvent event) {
        NotificationContext ctx = buildContext(event.getReservationReferenceNumber());
        String message = "Dear " + ctx.guest().getName() + ", thank you for staying at CodexHotel! " +
                "We hope to see you again soon. " +
                "Room " + ctx.room().getRoomNumber() + " checkout recorded on " + ctx.reservation().getCheckOutDate();
        log.info("[NOTIFICATION - CHECK-OUT] To: {} | {}", ctx.guest().getEmail(), message);
    }

    @EventListener
    public void onReservationCancelled(ReservationCancelledEvent event) {
        NotificationContext ctx = buildContext(event.getReservationReferenceNumber());
        String message = "Dear " + ctx.guest().getName() + ", your reservation (Ref: " +
                ctx.reservation().getReferenceNumber() + ") for Room " +
                ctx.room().getRoomNumber() + " has been cancelled successfully.";
        log.info("[NOTIFICATION - CANCELLATION] To: {} | {}", ctx.guest().getEmail(), message);
    }

    @EventListener
    public void onReservationReminder(ReservationReminderEvent event) {
        NotificationContext ctx = buildContext(event.getReservationReferenceNumber());
        String message = "Dear " + ctx.guest().getName() + ", this is a reminder for your upcoming stay at CodexHotel. " +
                "Room " + ctx.room().getRoomNumber() + " is reserved for you. " +
                "Check-in date: " + ctx.reservation().getCheckInDate() + ". " +
                "Reference: " + ctx.reservation().getReferenceNumber();
        log.info("[NOTIFICATION - REMINDER] To: {} | {}", ctx.guest().getEmail(), message);
    }

    @EventListener
    public void onCheckOutReminder(GuestCheckedOutReminderEvent event) {
        NotificationContext ctx = buildContext(event.getReservationReferenceNumber());
        String message = "Dear " + ctx.guest().getName() + ", reminder that your check-out from Room " +
                ctx.room().getRoomNumber() + " is tomorrow, " + ctx.reservation().getCheckOutDate() + ". " +
                "We hope you enjoyed your stay!";
        log.info("[NOTIFICATION - CHECKOUT REMINDER] To: {} | {}", ctx.guest().getEmail(), message);
    }

    @EventListener
    public void onReservationConfirmed(ReservationConfirmedEvent event) {
        NotificationContext ctx = buildContext(event.getReservationReferenceNumber());
        String message = "Dear " + ctx.guest().getName() + ", your payment has been confirmed! " +
                "Your reservation for Room " + ctx.room().getRoomNumber() + " is now confirmed. " +
                "Check-in date: " + ctx.reservation().getCheckInDate() + ". " +
                "Reference: " + ctx.reservation().getReferenceNumber();
        log.info("[NOTIFICATION - CONFIRMED] To: {} | {}", ctx.guest().getEmail(), message);
    }

    private NotificationContext buildContext(String referenceNumber) {
        Reservation reservation = reservationRepository.findByReferenceNumber(referenceNumber)
                .orElseThrow(() -> new ReservationNotFoundException("Reservation not found"));
        Guest guest = guestRepository.findById(reservation.getGuestId())
                .orElseThrow(() -> new GuestNotFoundException("Guest not found"));
        Room room = roomRepository.findById(reservation.getRoomId())
                .orElseThrow(() -> new RoomNotFoundException("Room not found"));
        return new NotificationContext(reservation, guest, room);
    }

    private record NotificationContext(Reservation reservation, Guest guest, Room room) {}
}