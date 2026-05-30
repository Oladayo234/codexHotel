package com.semicolon.codexHotel.unit;

import com.semicolon.codexHotel.data.models.Admin;
import com.semicolon.codexHotel.data.models.enums.Role;
import com.semicolon.codexHotel.data.repositories.AdminRepository;
import com.semicolon.codexHotel.dtos.requests.CreateFrontDeskRequest;
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