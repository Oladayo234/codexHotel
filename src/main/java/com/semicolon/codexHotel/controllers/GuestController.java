package com.semicolon.codexHotel.controllers;

import com.semicolon.codexHotel.dtos.requests.BookRoomRequest;
import com.semicolon.codexHotel.dtos.requests.LoginRequest;
import com.semicolon.codexHotel.dtos.requests.RegisterGuestRequest;
import com.semicolon.codexHotel.dtos.responses.BookRoomResponse;
import com.semicolon.codexHotel.dtos.responses.GuestLoginResponse;
import com.semicolon.codexHotel.dtos.responses.RegisterGuestResponse;
import com.semicolon.codexHotel.services.GuestService;
import com.semicolon.codexHotel.services.ReservationService;
import com.semicolon.codexHotel.services.RoomService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/guests")
@RequiredArgsConstructor
public class GuestController {

    private final GuestService guestService;
    private final ReservationService reservationService;
    private final RoomService roomService;

    @PostMapping("/register")
    public ResponseEntity<RegisterGuestResponse> register(@Valid @RequestBody RegisterGuestRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(guestService.register(request));
    }

    @PostMapping("/login")
    public ResponseEntity<GuestLoginResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.status(HttpStatus.OK).body(guestService.login(request));
    }

    @PostMapping("/book")
    public ResponseEntity<BookRoomResponse> bookRoom(@Valid @RequestBody BookRoomRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(reservationService.bookRoom(request));
    }

    @GetMapping("/bookings/{guestReferenceNumber}")
    public ResponseEntity<?> getReservationsByGuestId(@PathVariable String guestReferenceNumber) {
        return ResponseEntity.status(HttpStatus.OK).body(reservationService.getReservationsByGuestReferenceNumber(guestReferenceNumber));
    }

    @GetMapping("/rooms/available")
    public ResponseEntity<?> getAvailableRooms() {
        return ResponseEntity.status(HttpStatus.OK).body(roomService.getAllAvailableRooms());
    }
}