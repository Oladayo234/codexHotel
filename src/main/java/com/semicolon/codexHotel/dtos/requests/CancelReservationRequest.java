package com.semicolon.codexHotel.dtos.requests;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CancelReservationRequest {
    @NotBlank(message = "Reference number is required")
    private String referenceNumber;
}
