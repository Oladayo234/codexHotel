package com.semicolon.codexHotel.utils;

import com.semicolon.codexHotel.data.models.Guest;
import com.semicolon.codexHotel.dtos.requests.RegisterGuestRequest;
import com.semicolon.codexHotel.dtos.responses.LoginResponse;
import com.semicolon.codexHotel.dtos.responses.RegisterGuestResponse;

public class GuestMapper {

    public static Guest toGuest(RegisterGuestRequest request) {
        Guest guest = new Guest();
        guest.setName(request.getName());
        guest.setEmail(request.getEmail());
        guest.setPhoneNumber(request.getPhoneNumber());
        guest.setPassword(request.getPassword());
        return guest;
    }

    public static RegisterGuestResponse toRegisterGuestResponse(Guest guest) {
        RegisterGuestResponse response = new RegisterGuestResponse();
        response.setMessage("Registration successful");
        response.setGuestId(guest.getId());
        response.setName(guest.getName());
        response.setEmail(guest.getEmail());
        response.setPhoneNumber(guest.getPhoneNumber());
        return response;
    }

    public static LoginResponse toLoginResponse(Guest guest) {
        LoginResponse response = new LoginResponse();
        response.setMessage("Login successful");
        response.setId(guest.getId());
        response.setName(guest.getName());
        response.setEmail(guest.getEmail());
        response.setRole(guest.getRole());
        return response;
    }
}