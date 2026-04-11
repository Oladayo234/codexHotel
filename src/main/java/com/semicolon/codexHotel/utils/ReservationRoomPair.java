package com.semicolon.codexHotel.utils;

import com.semicolon.codexHotel.data.models.Reservation;
import com.semicolon.codexHotel.data.models.Room;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ReservationRoomPair {
    private Reservation reservation;
    private Room room;
}
