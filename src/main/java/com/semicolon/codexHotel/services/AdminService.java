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
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final AdminRepository adminRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    public FrontDeskLoginResponse createFrontDesk(CreateFrontDeskRequest request) {
        if (adminRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new InvalidCredentialsException("Front desk account with email " + request.getEmail() + " already exists");
        }
        Admin frontDesk = new Admin();
        frontDesk.setName(request.getName());
        frontDesk.setEmail(request.getEmail());
        frontDesk.setPassword(passwordEncoder.encode(request.getPassword()));
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