package com.semicolon.codexHotel.controllers;

import com.semicolon.codexHotel.dtos.requests.LoginRequest;
import com.semicolon.codexHotel.dtos.requests.RegisterGuestRequest;
import com.semicolon.codexHotel.dtos.responses.AuthResponse;
import com.semicolon.codexHotel.services.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/admin/login")
    public ResponseEntity<AuthResponse> adminLogin(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.status(HttpStatus.OK).body(authService.adminLogin(request));
    }

    @PostMapping("/frontdesk/login")
    public ResponseEntity<AuthResponse> frontDeskLogin(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.status(HttpStatus.OK).body(authService.frontDeskLogin(request));
    }

    @PostMapping("/guest/register")
    public ResponseEntity<AuthResponse> guestRegister(@Valid @RequestBody RegisterGuestRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(authService.guestRegister(request));
    }

    @PostMapping("/guest/login")
    public ResponseEntity<AuthResponse> guestLogin(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.status(HttpStatus.OK).body(authService.guestLogin(request));
    }
}