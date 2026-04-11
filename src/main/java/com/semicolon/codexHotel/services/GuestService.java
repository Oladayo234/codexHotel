package com.semicolon.codexHotel.services;

import com.semicolon.codexHotel.data.models.Guest;
import com.semicolon.codexHotel.data.repositories.GuestRepository;
import com.semicolon.codexHotel.dtos.requests.LoginRequest;
import com.semicolon.codexHotel.dtos.requests.RegisterGuestRequest;
import com.semicolon.codexHotel.dtos.responses.GuestLoginResponse;
import com.semicolon.codexHotel.dtos.responses.RegisterGuestResponse;
import com.semicolon.codexHotel.exceptions.*;
import com.semicolon.codexHotel.utils.GuestMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GuestService {

    private final GuestRepository guestRepository;

    public RegisterGuestResponse register(RegisterGuestRequest request) {
        if (guestRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new GuestAlreadyExistsException("Guest with email " + request.getEmail() + " already exists");
        }
        Guest guest = GuestMapper.toGuest(request);
        guestRepository.save(guest);
        return GuestMapper.toRegisterGuestResponse(guest);
    }

    public GuestLoginResponse login(LoginRequest request) {
        Guest guest = guestRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new GuestNotFoundException("Guest not found"));
        if (!guest.getPassword().equals(request.getPassword())) {
            throw new InvalidCredentialsException("Invalid credentials");
        }
        return GuestMapper.toLoginResponse(guest);
    }
}