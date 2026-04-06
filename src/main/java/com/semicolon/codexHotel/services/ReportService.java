package com.semicolon.codexHotel.services;

import com.semicolon.codexHotel.data.models.Reservation;
import com.semicolon.codexHotel.data.models.enums.RoomStatus;
import com.semicolon.codexHotel.data.repositories.ReservationRepository;
import com.semicolon.codexHotel.data.repositories.RoomRepository;
import com.semicolon.codexHotel.dtos.responses.ReportResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ReportService {

    private final ReservationRepository reservationRepository;
    private final RoomRepository roomRepository;

    public ReportResponse generateReport() {
        List<Reservation> allReservations = reservationRepository.findAll();
        long totalRooms = roomRepository.count();
        long occupiedRooms = roomRepository.countByRoomStatus(RoomStatus.OCCUPIED);

        double totalRevenue = 0;
        for (Reservation reservation : allReservations) {
            totalRevenue += reservation.getTotalPayment();
        }

        double occupancyRate = totalRooms == 0 ? 0 : ((double) occupiedRooms / totalRooms) * 100;

        ReportResponse response = new ReportResponse();
        response.setTotalRoomsBooked((int) occupiedRooms);
        response.setOccupancyRate(occupancyRate);
        response.setTotalRevenue(totalRevenue);
        return response;
    }
}