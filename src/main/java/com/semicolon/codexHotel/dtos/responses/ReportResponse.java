package com.semicolon.codexHotel.dtos.responses;

import lombok.Data;

@Data
public class ReportResponse {
    private int totalRoomsBooked;
    private double occupancyRate;
    private double totalRevenue;
}
