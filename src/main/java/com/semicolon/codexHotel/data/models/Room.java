package com.semicolon.codexHotel.data.models;

import com.semicolon.codexHotel.data.models.enums.RoomStatus;
import com.semicolon.codexHotel.data.models.enums.RoomType;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document(collection = "rooms")
public class Room {
    @Id
    private String id;
    private String roomNumber;
    private RoomType roomType;
    private RoomStatus roomStatus;
}
