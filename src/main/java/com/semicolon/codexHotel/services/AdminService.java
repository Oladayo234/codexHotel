package com.semicolon.codexHotel.services;

import com.semicolon.codexHotel.data.models.Admin;
import com.semicolon.codexHotel.data.models.enums.Role;
import com.semicolon.codexHotel.data.repositories.AdminRepository;
import com.semicolon.codexHotel.dtos.requests.CreateFrontDeskRequest;
import com.semicolon.codexHotel.dtos.requests.LoginRequest;
import com.semicolon.codexHotel.dtos.responses.AdminLoginResponse;
import com.semicolon.codexHotel.dtos.responses.FrontDeskLoginResponse;
import com.semicolon.codexHotel.exceptions.InvalidCredentialsException;
import com.semicolon.codexHotel.utils.FrontDeskReferenceGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final AdminRepository adminRepository;

    public AdminLoginResponse login(LoginRequest request) {
        Admin admin = adminRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new InvalidCredentialsException("Invalid credentials"));
        if (!admin.getPassword().equals(request.getPassword())) {
            throw new InvalidCredentialsException("Invalid credentials");
        }
        AdminLoginResponse response = new AdminLoginResponse();
        response.setMessage("Welcome, Madam Bolu!");
        response.setAdminReferenceNumber(admin.getAdminReferenceNumber());
        response.setName(admin.getName());
        response.setEmail(admin.getEmail());
        response.setRole(admin.getRole());
        return response;
    }

    public FrontDeskLoginResponse createFrontDesk(CreateFrontDeskRequest request) {
        if (adminRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new InvalidCredentialsException("Front desk account with email " + request.getEmail() + " already exists");
        }
        Admin frontDesk = new Admin();
        frontDesk.setName(request.getName());
        frontDesk.setEmail(request.getEmail());
        frontDesk.setPassword(request.getPassword());
        frontDesk.setRole(Role.FRONT_DESK);
        frontDesk.setAdminReferenceNumber(FrontDeskReferenceGenerator.generateFrontDeskReference());
        adminRepository.save(frontDesk);

        FrontDeskLoginResponse response = new FrontDeskLoginResponse();
        response.setMessage("Front desk account created successfully");
        response.setFrontDeskReferenceNumber(frontDesk.getAdminReferenceNumber());
        response.setName(frontDesk.getName());
        response.setEmail(frontDesk.getEmail());
        response.setRole(frontDesk.getRole());
        return response;
    }
}