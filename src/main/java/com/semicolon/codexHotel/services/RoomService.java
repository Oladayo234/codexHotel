package com.semicolon.codexHotel.services;

import com.semicolon.codexHotel.data.models.Room;
import com.semicolon.codexHotel.data.models.enums.RoomStatus;
import com.semicolon.codexHotel.data.models.enums.RoomType;
import com.semicolon.codexHotel.data.repositories.RoomRepository;
import com.semicolon.codexHotel.dtos.requests.AddRoomRequest;
import com.semicolon.codexHotel.dtos.responses.RoomResponse;
import com.semicolon.codexHotel.exceptions.RoomAlreadyExistException;
import com.semicolon.codexHotel.exceptions.RoomNotAvailableException;
import com.semicolon.codexHotel.exceptions.RoomNotFoundException;
import com.semicolon.codexHotel.utils.GenerateRoomNumber;
import com.semicolon.codexHotel.utils.RoomMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RoomService {

    private final RoomRepository roomRepository;

    public RoomResponse addRoom(AddRoomRequest request) {
        String roomNumber = GenerateRoomNumber.generateRoomNumber(request.getRoomType());
        if (roomRepository.findByRoomNumber(roomNumber).isPresent()) {
            roomNumber = GenerateRoomNumber.generateRoomNumber(request.getRoomType());
        }
        Room room = RoomMapper.toRoom(request);
        room.setRoomNumber(roomNumber);
        roomRepository.save(room);
        return RoomMapper.toRoomResponse(room);
    }

    public List<RoomResponse> getAllAvailableRooms() {
        List<Room> rooms = roomRepository.findByRoomStatus(RoomStatus.AVAILABLE);
        List<RoomResponse> responses = new ArrayList<>();
        for (Room room : rooms) {
            responses.add(RoomMapper.toRoomResponse(room));
        }
        return responses;
    }

    public List<RoomResponse> getAllRooms() {
        List<Room> rooms = roomRepository.findAll();
        List<RoomResponse> responses = new ArrayList<>();
        for (Room room : rooms) {
            responses.add(RoomMapper.toRoomResponse(room));
        }
        return responses;
    }

    public RoomResponse markRoomForMaintenance(String roomNumber) {
        Room room = roomRepository.findByRoomNumber(roomNumber)
                .orElseThrow(() -> new RoomNotFoundException("Room " + roomNumber + " not found"));
        room.setRoomStatus(RoomStatus.MAINTENANCE);
        roomRepository.save(room);
        return RoomMapper.toRoomResponse(room);
    }

    public RoomResponse markRoomAsAvailable(String roomNumber) {
        Room room = roomRepository.findByRoomNumber(roomNumber)
                .orElseThrow(() -> new RoomNotFoundException("Room " + roomNumber + " not found"));
        if (room.getRoomStatus() != RoomStatus.MAINTENANCE) {
            throw new RoomNotAvailableException("Room " + roomNumber + " is not under maintenance");
        }
        room.setRoomStatus(RoomStatus.AVAILABLE);
        roomRepository.save(room);
        return RoomMapper.toRoomResponse(room);
    }

    public List<RoomResponse> getRoomsByType(RoomType roomType) {
        List<Room> rooms = roomRepository.findByRoomType(roomType);
        List<RoomResponse> responses = new ArrayList<>();
        for (Room room : rooms) {
            responses.add(RoomMapper.toRoomResponse(room));
        }
        return responses;
    }
}