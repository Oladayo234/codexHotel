package com.semicolon.codexHotel.utils;

import com.semicolon.codexHotel.data.models.Guest;
import com.semicolon.codexHotel.dtos.requests.RegisterGuestRequest;
import com.semicolon.codexHotel.dtos.responses.GuestLoginResponse;
import com.semicolon.codexHotel.dtos.responses.RegisterGuestResponse;

public class GuestMapper {

    public static Guest toGuest(RegisterGuestRequest request) {
        Guest guest = new Guest();
        guest.setName(request.getName());
        guest.setEmail(request.getEmail());
        guest.setPhoneNumber(request.getPhoneNumber());
        guest.setGuestReferenceNumber(GuestReferenceGenerator.generateGuestReference());
        guest.setPassword(request.getPassword());
        return guest;
    }

    public static RegisterGuestResponse toRegisterGuestResponse(Guest guest) {
        RegisterGuestResponse response = new RegisterGuestResponse();
        response.setMessage("Registration successful");
        response.setName(guest.getName());
        response.setEmail(guest.getEmail());
        response.setPhoneNumber(guest.getPhoneNumber());
        response.setGuestReferenceNumber(guest.getGuestReferenceNumber());
        return response;
    }

    public static GuestLoginResponse toLoginResponse(Guest guest) {
        GuestLoginResponse response = new GuestLoginResponse();
        response.setMessage("Login successful");
        response.setGuestReferenceNumber(guest.getGuestReferenceNumber());
        response.setName(guest.getName());
        response.setEmail(guest.getEmail());
        response.setRole(guest.getRole());
        return response;
    }
}