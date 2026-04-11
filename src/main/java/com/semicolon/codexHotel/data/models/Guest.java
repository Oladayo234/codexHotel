package com.semicolon.codexHotel.data.models;

import com.semicolon.codexHotel.data.models.enums.Role;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "guests")
@Data
public class Guest {
    @Id
    private String id;
    private String name;
    private String email;
    private String phoneNumber;
    private String password;
    private String guestReferenceNumber;
    private Role role = Role.GUEST;
}
