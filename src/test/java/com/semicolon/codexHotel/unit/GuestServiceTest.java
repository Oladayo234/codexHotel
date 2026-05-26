package com.semicolon.codexHotel.unit;

import com.semicolon.codexHotel.data.models.Guest;
import com.semicolon.codexHotel.data.repositories.GuestRepository;
import com.semicolon.codexHotel.dtos.requests.LoginRequest;
import com.semicolon.codexHotel.dtos.requests.RegisterGuestRequest;
import com.semicolon.codexHotel.dtos.responses.GuestLoginResponse;
import com.semicolon.codexHotel.dtos.responses.RegisterGuestResponse;
import com.semicolon.codexHotel.exceptions.GuestAlreadyExistsException;
import com.semicolon.codexHotel.exceptions.GuestNotFoundException;
import com.semicolon.codexHotel.exceptions.InvalidCredentialsException;
import com.semicolon.codexHotel.services.GuestService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GuestServiceTest {

    @Mock
    private GuestRepository guestRepository;

    @Mock
    private BCryptPasswordEncoder passwordEncoder;

    @InjectMocks
    private GuestService guestService;

    // ── register ─────────────────────────────────────────────────────────────

    @Test
    void register_newGuest_savesGuestAndReturnsSuccessResponse() {
        RegisterGuestRequest request = new RegisterGuestRequest();
        request.setName("John Doe");
        request.setEmail("john@example.com");
        request.setPhoneNumber("08012345678");
        request.setPassword("Password@1");

        when(guestRepository.findByEmail("john@example.com")).thenReturn(Optional.empty());
        when(guestRepository.save(any(Guest.class))).thenAnswer(inv -> inv.getArgument(0));

        RegisterGuestResponse response = guestService.register(request);

        assertEquals("Registration successful", response.getMessage());
        assertEquals("John Doe", response.getName());
        assertEquals("john@example.com", response.getEmail());
        assertEquals("08012345678", response.getPhoneNumber());
        assertNotNull(response.getGuestReferenceNumber());
        assertTrue(response.getGuestReferenceNumber().startsWith("GST-"));
        verify(guestRepository).save(any(Guest.class));
    }

    @Test
    void register_duplicateEmail_throwsGuestAlreadyExistsException() {
        RegisterGuestRequest request = new RegisterGuestRequest();
        request.setEmail("existing@example.com");

        when(guestRepository.findByEmail("existing@example.com"))
                .thenReturn(Optional.of(new Guest()));

        assertThrows(GuestAlreadyExistsException.class, () -> guestService.register(request));
        verify(guestRepository, never()).save(any());
    }

    // ── login ─────────────────────────────────────────────────────────────────

    @Test
    void login_guestNotFound_throwsGuestNotFoundException() {
        LoginRequest request = new LoginRequest();
        request.setEmail("nobody@example.com");
        request.setPassword("Password@1");

        when(guestRepository.findByEmail("nobody@example.com")).thenReturn(Optional.empty());

        assertThrows(GuestNotFoundException.class, () -> guestService.login(request));
    }

    @Test
    void login_wrongPassword_throwsInvalidCredentialsException() {
        Guest guest = new Guest();
        guest.setEmail("john@example.com");
        guest.setPassword("$2a$10$storedBcryptHash");

        LoginRequest request = new LoginRequest();
        request.setEmail("john@example.com");
        request.setPassword("WrongPass@1");

        when(guestRepository.findByEmail("john@example.com")).thenReturn(Optional.of(guest));
        when(passwordEncoder.matches("WrongPass@1", "$2a$10$storedBcryptHash")).thenReturn(false);

        assertThrows(InvalidCredentialsException.class, () -> guestService.login(request));
    }

    @Test
    void login_correctPassword_returnsGuestLoginResponse() {
        Guest guest = new Guest();
        guest.setEmail("john@example.com");
        guest.setName("John Doe");
        guest.setGuestReferenceNumber("GST-ABCD1234");
        guest.setPassword("$2a$10$storedBcryptHash");

        LoginRequest request = new LoginRequest();
        request.setEmail("john@example.com");
        request.setPassword("Password@1");

        when(guestRepository.findByEmail("john@example.com")).thenReturn(Optional.of(guest));
        when(passwordEncoder.matches("Password@1", "$2a$10$storedBcryptHash")).thenReturn(true);

        GuestLoginResponse response = guestService.login(request);

        assertEquals("Login successful", response.getMessage());
        assertEquals("GST-ABCD1234", response.getGuestReferenceNumber());
        assertEquals("John Doe", response.getName());
    }
}