package com.semicolon.codexHotel.utils;

import com.semicolon.codexHotel.data.models.Room;
import com.semicolon.codexHotel.data.models.enums.RoomStatus;
import com.semicolon.codexHotel.dtos.requests.AddRoomRequest;
import com.semicolon.codexHotel.dtos.responses.RoomResponse;

public class RoomMapper {

    public static Room toRoom(AddRoomRequest request) {
        Room room = new Room();/*
        room.setRoomNumber(request.getRoomNumber());*/
        room.setRoomType(request.getRoomType());
        room.setRoomStatus(RoomStatus.AVAILABLE);
        return room;
    }

    public static RoomResponse toRoomResponse(Room room) {
        RoomResponse response = new RoomResponse();
        response.setRoomNumber(room.getRoomNumber());
        response.setRoomType(room.getRoomType());
        response.setPricePerNight(room.getRoomType().getPricePerNight());
        response.setRoomStatus(room.getRoomStatus());
        return response;
    }
}