package com.semicolon.codexHotel.data.repositories;

import com.semicolon.codexHotel.data.models.Room;
import com.semicolon.codexHotel.data.models.enums.RoomStatus;
import com.semicolon.codexHotel.data.models.enums.RoomType;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface RoomRepository extends MongoRepository<Room,String> {
    List<Room> findByRoomStatus(RoomStatus roomStatus);
    List<Room> findByRoomType(RoomType roomType);
    Optional<Room> findByRoomNumber(String roomNumber);
    long countByRoomStatus(RoomStatus roomStatus);
}
