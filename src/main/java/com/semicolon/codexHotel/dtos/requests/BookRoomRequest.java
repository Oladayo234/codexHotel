package com.semicolon.codexHotel.dtos.requests;

import com.semicolon.codexHotel.data.models.enums.RoomType;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class BookRoomRequest {
    @NotBlank(message = "Guest reference number is required")
    private String guestReferenceNumber;
    @NotNull(message = "Room type is required")
    private RoomType roomType;
    @Min(value = 1, message = "Number of nights must be at least 1")
    private int numberOfNights;
}
