package com.semicolon.codexHotel.dtos.requests;

import com.semicolon.codexHotel.data.models.enums.RoomType;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AddRoomRequest {
    @NotNull(message = "Room type is required")
    private RoomType roomType;
}
