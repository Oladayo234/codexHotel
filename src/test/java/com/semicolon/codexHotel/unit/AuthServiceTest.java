package com.semicolon.codexHotel.unit;

import com.semicolon.codexHotel.data.models.Admin;
import com.semicolon.codexHotel.data.models.Guest;
import com.semicolon.codexHotel.data.models.enums.Role;
import com.semicolon.codexHotel.data.repositories.AdminRepository;
import com.semicolon.codexHotel.data.repositories.GuestRepository;
import com.semicolon.codexHotel.dtos.requests.LoginRequest;
import com.semicolon.codexHotel.dtos.requests.RegisterGuestRequest;
import com.semicolon.codexHotel.dtos.responses.AuthResponse;
import com.semicolon.codexHotel.exceptions.GuestAlreadyExistsException;
import com.semicolon.codexHotel.exceptions.InvalidCredentialsException;
import com.semicolon.codexHotel.security.JwtService;
import com.semicolon.codexHotel.services.AuthService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock private AdminRepository adminRepository;
    @Mock private GuestRepository guestRepository;
    @Mock private BCryptPasswordEncoder passwordEncoder;
    @Mock private JwtService jwtService;

    @InjectMocks
    private AuthService authService;

    // ── adminLogin ─────────────────────────────────────────────────────────────

    @Test
    void adminLogin_validCredentials_returnsAuthResponse() {
        Admin admin = new Admin();
        admin.setEmail("admin@codexhotel.com");
        admin.setPassword("$2a$10$hashedPassword");
        admin.setName("Madam Bolu");
        admin.setAdminReferenceNumber("ADM-TESTXXXX");
        admin.setRole(Role.ADMIN);

        LoginRequest request = new LoginRequest();
        request.setEmail("admin@codexhotel.com");
        request.setPassword("Admin@1234");

        when(adminRepository.findByEmail("admin@codexhotel.com")).thenReturn(Optional.of(admin));
        when(passwordEncoder.matches("Admin@1234", "$2a$10$hashedPassword")).thenReturn(true);
        when(jwtService.generateToken("admin@codexhotel.com", "ADMIN")).thenReturn("jwt-token");

        AuthResponse response = authService.adminLogin(request);

        assertEquals("jwt-token", response.getToken());
        assertEquals("admin@codexhotel.com", response.getEmail());
        assertEquals("Madam Bolu", response.getName());
        assertEquals("ADMIN", response.getRole());
        assertEquals("ADM-TESTXXXX", response.getReferenceNumber());
    }

    @Test
    void adminLogin_emailNotFound_throwsInvalidCredentialsException() {
        LoginRequest request = new LoginRequest();
        request.setEmail("ghost@example.com");
        request.setPassword("Password@1");

        when(adminRepository.findByEmail("ghost@example.com")).thenReturn(Optional.empty());

        assertThrows(InvalidCredentialsException.class, () -> authService.adminLogin(request));
    }

    @Test
    void adminLogin_wrongPassword_throwsInvalidCredentialsException() {
        Admin admin = new Admin();
        admin.setEmail("admin@codexhotel.com");
        admin.setPassword("$2a$10$hashedPassword");

        LoginRequest request = new LoginRequest();
        request.setEmail("admin@codexhotel.com");
        request.setPassword("WrongPassword");

        when(adminRepository.findByEmail("admin@codexhotel.com")).thenReturn(Optional.of(admin));
        when(passwordEncoder.matches("WrongPassword", "$2a$10$hashedPassword")).thenReturn(false);

        assertThrows(InvalidCredentialsException.class, () -> authService.adminLogin(request));
    }

    // ── frontDeskLogin ─────────────────────────────────────────────────────────

    @Test
    void frontDeskLogin_validCredentials_returnsAuthResponse() {
        Admin frontDesk = new Admin();
        frontDesk.setEmail("alice@codexhotel.com");
        frontDesk.setPassword("$2a$10$hashedPassword");
        frontDesk.setName("Alice");
        frontDesk.setAdminReferenceNumber("FD-XXXX1234");
        frontDesk.setRole(Role.FRONT_DESK);

        LoginRequest request = new LoginRequest();
        request.setEmail("alice@codexhotel.com");
        request.setPassword("Alice@1234");

        when(adminRepository.findByEmail("alice@codexhotel.com")).thenReturn(Optional.of(frontDesk));
        when(passwordEncoder.matches("Alice@1234", "$2a$10$hashedPassword")).thenReturn(true);
        when(jwtService.generateToken("alice@codexhotel.com", "FRONT_DESK")).thenReturn("fd-jwt-token");

        AuthResponse response = authService.frontDeskLogin(request);

        assertEquals("fd-jwt-token", response.getToken());
        assertEquals("FRONT_DESK", response.getRole());
        assertEquals("FD-XXXX1234", response.getReferenceNumber());
    }

    @Test
    void frontDeskLogin_emailNotFound_throwsInvalidCredentialsException() {
        LoginRequest request = new LoginRequest();
        request.setEmail("nobody@codexhotel.com");
        request.setPassword("Password@1");

        when(adminRepository.findByEmail("nobody@codexhotel.com")).thenReturn(Optional.empty());

        assertThrows(InvalidCredentialsException.class, () -> authService.frontDeskLogin(request));
    }

    @Test
    void frontDeskLogin_wrongPassword_throwsInvalidCredentialsException() {
        Admin frontDesk = new Admin();
        frontDesk.setEmail("alice@codexhotel.com");
        frontDesk.setPassword("$2a$10$hashedPassword");

        LoginRequest request = new LoginRequest();
        request.setEmail("alice@codexhotel.com");
        request.setPassword("WrongPassword");

        when(adminRepository.findByEmail("alice@codexhotel.com")).thenReturn(Optional.of(frontDesk));
        when(passwordEncoder.matches("WrongPassword", "$2a$10$hashedPassword")).thenReturn(false);

        assertThrows(InvalidCredentialsException.class, () -> authService.frontDeskLogin(request));
    }

    // ── guestRegister ──────────────────────────────────────────────────────────

    @Test
    void guestRegister_newGuest_returnsAuthResponse() {
        RegisterGuestRequest request = new RegisterGuestRequest();
        request.setName("John Doe");
        request.setEmail("john@example.com");
        request.setPhoneNumber("08012345678");
        request.setPassword("Password@1");

        when(guestRepository.findByEmail("john@example.com")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("Password@1")).thenReturn("$2a$10$encodedPassword");
        when(guestRepository.save(any(Guest.class))).thenAnswer(inv -> inv.getArgument(0));
        when(jwtService.generateToken(anyString(), anyString())).thenReturn("guest-jwt-token");

        AuthResponse response = authService.guestRegister(request);

        assertEquals("guest-jwt-token", response.getToken());
        assertEquals("john@example.com", response.getEmail());
        assertEquals("John Doe", response.getName());
        assertEquals("GUEST", response.getRole());
        assertNotNull(response.getReferenceNumber());
        assertTrue(response.getReferenceNumber().startsWith("GST-"));
    }

    @Test
    void guestRegister_duplicateEmail_throwsGuestAlreadyExistsException() {
        RegisterGuestRequest request = new RegisterGuestRequest();
        request.setEmail("existing@example.com");

        when(guestRepository.findByEmail("existing@example.com"))
                .thenReturn(Optional.of(new Guest()));

        assertThrows(GuestAlreadyExistsException.class, () -> authService.guestRegister(request));
        verify(guestRepository, never()).save(any());
    }

    // ── guestLogin ─────────────────────────────────────────────────────────────

    @Test
    void guestLogin_validCredentials_returnsAuthResponse() {
        Guest guest = new Guest();
        guest.setEmail("john@example.com");
        guest.setName("John Doe");
        guest.setGuestReferenceNumber("GST-ABCD1234");
        guest.setPassword("$2a$10$storedHash");
        guest.setRole(Role.GUEST);

        LoginRequest request = new LoginRequest();
        request.setEmail("john@example.com");
        request.setPassword("Password@1");

        when(guestRepository.findByEmail("john@example.com")).thenReturn(Optional.of(guest));
        when(passwordEncoder.matches("Password@1", "$2a$10$storedHash")).thenReturn(true);
        when(jwtService.generateToken("john@example.com", "GUEST")).thenReturn("guest-token");

        AuthResponse response = authService.guestLogin(request);

        assertEquals("guest-token", response.getToken());
        assertEquals("GST-ABCD1234", response.getReferenceNumber());
        assertEquals("John Doe", response.getName());
    }

    @Test
    void guestLogin_emailNotFound_throwsInvalidCredentialsException() {
        LoginRequest request = new LoginRequest();
        request.setEmail("nobody@example.com");
        request.setPassword("Password@1");

        when(guestRepository.findByEmail("nobody@example.com")).thenReturn(Optional.empty());

        assertThrows(InvalidCredentialsException.class, () -> authService.guestLogin(request));
    }

    @Test
    void guestLogin_wrongPassword_throwsInvalidCredentialsException() {
        Guest guest = new Guest();
        guest.setEmail("john@example.com");
        guest.setPassword("$2a$10$storedHash");

        LoginRequest request = new LoginRequest();
        request.setEmail("john@example.com");
        request.setPassword("WrongPass@1");

        when(guestRepository.findByEmail("john@example.com")).thenReturn(Optional.of(guest));
        when(passwordEncoder.matches("WrongPass@1", "$2a$10$storedHash")).thenReturn(false);

        assertThrows(InvalidCredentialsException.class, () -> authService.guestLogin(request));
    }
}