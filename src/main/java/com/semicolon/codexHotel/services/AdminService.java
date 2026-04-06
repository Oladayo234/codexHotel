package com.semicolon.codexHotel.services;

import com.semicolon.codexHotel.data.models.Admin;
import com.semicolon.codexHotel.data.models.enums.Role;
import com.semicolon.codexHotel.data.repositories.AdminRepository;
import com.semicolon.codexHotel.dtos.requests.CreateFrontDeskRequest;
import com.semicolon.codexHotel.dtos.requests.LoginRequest;
import com.semicolon.codexHotel.dtos.responses.LoginResponse;
import com.semicolon.codexHotel.exceptions.InvalidCredentialsException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final AdminRepository adminRepository;

    public LoginResponse login(LoginRequest request) {
        Admin admin = adminRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new InvalidCredentialsException("Invalid credentials"));
        if (!admin.getPassword().equals(request.getPassword())) {
            throw new InvalidCredentialsException("Invalid credentials");
        }
        LoginResponse response = new LoginResponse();
        response.setMessage("Welcome, Madam Bolu!");
        response.setId(admin.getId());
        response.setName(admin.getName());
        response.setEmail(admin.getEmail());
        response.setRole(admin.getRole());
        return response;
    }

    public LoginResponse createFrontDesk(CreateFrontDeskRequest request) {
        if (adminRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new InvalidCredentialsException("Front desk account with email " + request.getEmail() + " already exists");
        }
        Admin frontDesk = new Admin();
        frontDesk.setName(request.getName());
        frontDesk.setEmail(request.getEmail());
        frontDesk.setPassword(request.getPassword());
        frontDesk.setRole(Role.FRONT_DESK);
        adminRepository.save(frontDesk);

        LoginResponse response = new LoginResponse();
        response.setMessage("Front desk account created successfully");
        response.setId(frontDesk.getId());
        response.setName(frontDesk.getName());
        response.setEmail(frontDesk.getEmail());
        response.setRole(frontDesk.getRole());
        return response;
    }
}