package com.semicolon.codexHotel.data.models;

import com.semicolon.codexHotel.data.models.enums.Role;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;


@Data
@Document(collection = "admins")
public class Admin {
    @Id
    private  String id;
    private String name;
    private String adminReferenceNumber;
    private String email;
    private String password;
    private Role role = Role.ADMIN;
}
