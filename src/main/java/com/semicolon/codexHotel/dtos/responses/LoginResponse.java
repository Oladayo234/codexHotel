package com.semicolon.codexHotel.dtos.responses;

import com.semicolon.codexHotel.data.models.enums.Role;
import lombok.Data;

@Data
public class LoginResponse {
    private String message;
    private String id;
    private String name;
    private String email;
    private Role role;
}
