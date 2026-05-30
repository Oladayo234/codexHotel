package com.semicolon.codexHotel.services;

import com.semicolon.codexHotel.data.models.Admin;
import com.semicolon.codexHotel.data.models.Guest;
import com.semicolon.codexHotel.data.repositories.AdminRepository;
import com.semicolon.codexHotel.data.repositories.GuestRepository;
import com.semicolon.codexHotel.dtos.requests.LoginRequest;
import com.semicolon.codexHotel.dtos.requests.RegisterGuestRequest;
import com.semicolon.codexHotel.dtos.responses.AuthResponse;
import com.semicolon.codexHotel.exceptions.GuestAlreadyExistsException;
import com.semicolon.codexHotel.exceptions.InvalidCredentialsException;
import com.semicolon.codexHotel.security.JwtService;
import com.semicolon.codexHotel.utils.GuestMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AdminRepository adminRepository;
    private final GuestRepository guestRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthResponse adminLogin(LoginRequest request) {
        Admin admin = adminRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new InvalidCredentialsException("Invalid credentials"));
        if (!passwordEncoder.matches(request.getPassword(), admin.getPassword())) {
            throw new InvalidCredentialsException("Invalid credentials");
        }
        String token = jwtService.generateToken(admin.getEmail(), admin.getRole().name());
        return new AuthResponse(
                token,
                admin.getEmail(),
                admin.getName(),
                admin.getRole().name(),
                admin.getAdminReferenceNumber()
        );
    }

    public AuthResponse frontDeskLogin(LoginRequest request) {
        Admin frontDesk = adminRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new InvalidCredentialsException("Invalid credentials"));
        if (!passwordEncoder.matches(request.getPassword(), frontDesk.getPassword())) {
            throw new InvalidCredentialsException("Invalid credentials");
        }
        String token = jwtService.generateToken(frontDesk.getEmail(), frontDesk.getRole().name());
        return new AuthResponse(
                token,
                frontDesk.getEmail(),
                frontDesk.getName(),
                frontDesk.getRole().name(),
                frontDesk.getAdminReferenceNumber()
        );
    }

    public AuthResponse guestRegister(RegisterGuestRequest request) {
        if (guestRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new GuestAlreadyExistsException("Guest with email " + request.getEmail() + " already exists");
        }
        Guest guest = GuestMapper.toGuest(request);
        guest.setPassword(passwordEncoder.encode(request.getPassword()));
        guestRepository.save(guest);
        String token = jwtService.generateToken(guest.getEmail(), guest.getRole().name());
        return new AuthResponse(
                token,
                guest.getEmail(),
                guest.getName(),
                guest.getRole().name(),
                guest.getGuestReferenceNumber()
        );
    }

    public AuthResponse guestLogin(LoginRequest request) {
        Guest guest = guestRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new InvalidCredentialsException("Invalid credentials"));
        if (!passwordEncoder.matches(request.getPassword(), guest.getPassword())) {
            throw new InvalidCredentialsException("Invalid credentials");
        }
        String token = jwtService.generateToken(guest.getEmail(), guest.getRole().name());
        return new AuthResponse(
                token,
                guest.getEmail(),
                guest.getName(),
                guest.getRole().name(),
                guest.getGuestReferenceNumber()
        );
    }
}