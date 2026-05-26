package com.semicolon.codexHotel.unit;

import com.semicolon.codexHotel.data.models.Admin;
import com.semicolon.codexHotel.data.models.enums.Role;
import com.semicolon.codexHotel.data.repositories.AdminRepository;
import com.semicolon.codexHotel.dtos.requests.CreateFrontDeskRequest;
import com.semicolon.codexHotel.dtos.requests.LoginRequest;
import com.semicolon.codexHotel.dtos.responses.AdminLoginResponse;
import com.semicolon.codexHotel.dtos.responses.FrontDeskLoginResponse;
import com.semicolon.codexHotel.exceptions.InvalidCredentialsException;
import com.semicolon.codexHotel.services.AdminService;
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
class AdminServiceTest {

    @Mock
    private AdminRepository adminRepository;

    @Mock
    private BCryptPasswordEncoder passwordEncoder;

    @InjectMocks
    private AdminService adminService;

    // ── login ─────────────────────────────────────────────────────────────────

    @Test
    void login_validPlainTextPassword_returnsAdminLoginResponse() {
        // AdminService compares passwords with plain .equals(), not BCrypt
        Admin admin = new Admin();
        admin.setEmail("admin@codexhotel.com");
        admin.setPassword("Admin@1234");   // stored as plain text for this comparison to work
        admin.setName("Madam Bolu");
        admin.setAdminReferenceNumber("ADM-TESTXXXX");
        admin.setRole(Role.ADMIN);

        LoginRequest request = new LoginRequest();
        request.setEmail("admin@codexhotel.com");
        request.setPassword("Admin@1234");

        when(adminRepository.findByEmail("admin@codexhotel.com")).thenReturn(Optional.of(admin));

        AdminLoginResponse response = adminService.login(request);

        assertEquals("Welcome, Madam Bolu!", response.getMessage());
        assertEquals("Madam Bolu", response.getName());
        assertEquals("admin@codexhotel.com", response.getEmail());
        assertEquals(Role.ADMIN, response.getRole());
    }

    @Test
    void login_emailNotFound_throwsInvalidCredentialsException() {
        LoginRequest request = new LoginRequest();
        request.setEmail("ghost@example.com");
        request.setPassword("Password@1");

        when(adminRepository.findByEmail("ghost@example.com")).thenReturn(Optional.empty());

        assertThrows(InvalidCredentialsException.class, () -> adminService.login(request));
    }

    @Test
    void login_wrongPassword_throwsInvalidCredentialsException() {
        Admin admin = new Admin();
        admin.setEmail("admin@codexhotel.com");
        admin.setPassword("CorrectPassword");

        LoginRequest request = new LoginRequest();
        request.setEmail("admin@codexhotel.com");
        request.setPassword("WrongPassword");

        when(adminRepository.findByEmail("admin@codexhotel.com")).thenReturn(Optional.of(admin));

        assertThrows(InvalidCredentialsException.class, () -> adminService.login(request));
    }

    // ── createFrontDesk ───────────────────────────────────────────────────────

    @Test
    void createFrontDesk_newEmail_savesStaffAndReturnsResponse() {
        CreateFrontDeskRequest request = new CreateFrontDeskRequest();
        request.setName("Alice");
        request.setEmail("alice@codexhotel.com");
        request.setPassword("Alice@1234");

        when(adminRepository.findByEmail("alice@codexhotel.com")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("Alice@1234")).thenReturn("$2a$10$encodedAlice");
        when(adminRepository.save(any(Admin.class))).thenAnswer(inv -> inv.getArgument(0));

        FrontDeskLoginResponse response = adminService.createFrontDesk(request);

        assertEquals("Front desk account created successfully", response.getMessage());
        assertEquals("Alice", response.getName());
        assertEquals("alice@codexhotel.com", response.getEmail());
        assertEquals(Role.FRONT_DESK, response.getRole());
        assertNotNull(response.getFrontDeskReferenceNumber());
        assertTrue(response.getFrontDeskReferenceNumber().startsWith("FD-"));
        verify(adminRepository).save(argThat(a ->
                a.getRole() == Role.FRONT_DESK &&
                "$2a$10$encodedAlice".equals(a.getPassword())
        ));
    }

    @Test
    void createFrontDesk_duplicateEmail_throwsInvalidCredentialsException() {
        CreateFrontDeskRequest request = new CreateFrontDeskRequest();
        request.setEmail("existing@codexhotel.com");

        when(adminRepository.findByEmail("existing@codexhotel.com"))
                .thenReturn(Optional.of(new Admin()));

        assertThrows(InvalidCredentialsException.class, () -> adminService.createFrontDesk(request));
        verify(adminRepository, never()).save(any());
    }
}