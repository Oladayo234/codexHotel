package com.semicolon.codexHotel.dtos.responses;

import com.semicolon.codexHotel.data.models.enums.RoomStatus;
import com.semicolon.codexHotel.data.models.enums.RoomType;
import lombok.Data;

@Data
public class RoomResponse {
    private String roomNumber;
    private RoomType roomType;
    private double pricePerNight;
    private RoomStatus roomStatus;
}
