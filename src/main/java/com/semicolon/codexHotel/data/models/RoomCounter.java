package com.semicolon.codexHotel.data.models;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document(collection = "room_counters")
public class RoomCounter {
    @Id
    private String id;
    private int count;
}
