package com.semicolon.codexHotel.controllers;

import com.semicolon.codexHotel.dtos.requests.BookRoomRequest;
import com.semicolon.codexHotel.dtos.requests.CancelReservationRequest;
import com.semicolon.codexHotel.dtos.requests.LoginRequest;
import com.semicolon.codexHotel.dtos.requests.ReceptionRequest;
import com.semicolon.codexHotel.dtos.responses.*;
import com.semicolon.codexHotel.services.AdminService;
import com.semicolon.codexHotel.services.FrontDeskService;
import com.semicolon.codexHotel.services.NotificationService;
import com.semicolon.codexHotel.services.ReservationService;
import com.semicolon.codexHotel.services.RoomService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/frontdesk")
@RequiredArgsConstructor
public class FrontDeskController {

    private final FrontDeskService frontDeskService;
    private final ReservationService reservationService;
    private final NotificationService notificationService;
    private final RoomService roomService;
    private final AdminService adminService;

    @PostMapping("/login")
    public ResponseEntity<AdminLoginResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.status(HttpStatus.OK).body(adminService.login(request));
    }

    @PostMapping("/book")
    public ResponseEntity<BookRoomResponse> bookRoom(@Valid @RequestBody BookRoomRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(reservationService.bookRoom(request));
    }

    @PostMapping("/checkin")
    public ResponseEntity<ReceptionResponse> checkIn(@Valid @RequestBody ReceptionRequest request) {
        return ResponseEntity.status(HttpStatus.OK).body(frontDeskService.checkIn(request));
    }

    @PostMapping("/checkout")
    public ResponseEntity<ReceptionResponse> checkOut(@Valid @RequestBody ReceptionRequest request) {
        return ResponseEntity.status(HttpStatus.OK).body(frontDeskService.checkOut(request));
    }

    @PatchMapping("/cancel")
    public ResponseEntity<CancelReservationResponse> cancelReservation(@Valid @RequestBody CancelReservationRequest request) {
        return ResponseEntity.status(HttpStatus.OK).body(frontDeskService.cancelReservation(request));
    }

    @PatchMapping("/rooms/{roomNumber}/maintenance")
    public ResponseEntity<RoomResponse> markRoomForMaintenance(@PathVariable String roomNumber) {
        return ResponseEntity.status(HttpStatus.OK).body(roomService.markRoomForMaintenance(roomNumber));
    }

    @PatchMapping("/rooms/{roomNumber}/available")
    public ResponseEntity<RoomResponse> markRoomAsAvailable(@PathVariable String roomNumber) {
        return ResponseEntity.status(HttpStatus.OK).body(roomService.markRoomAsAvailable(roomNumber));
    }

    @PostMapping("/notify/{referenceNumber}")
    public ResponseEntity<NotificationResponse> sendNotification(@PathVariable String referenceNumber) {
        return ResponseEntity.status(HttpStatus.OK).body(notificationService.sendReminder(referenceNumber));
    }

    @GetMapping("/rooms/available")
    public ResponseEntity<List<RoomResponse>> getAvailableRooms() {
        return ResponseEntity.status(HttpStatus.OK).body(roomService.getAllAvailableRooms());
    }

    @GetMapping("/guests/{guestReferenceNumber}")
    public ResponseEntity<?> getGuestDetails(@PathVariable String guestReferenceNumber) {
        return ResponseEntity.status(HttpStatus.OK).body(reservationService.getReservationsByGuestReferenceNumber(guestReferenceNumber));
    }
}