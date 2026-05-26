package com.semicolon.codexHotel.unit;

import com.semicolon.codexHotel.data.models.Room;
import com.semicolon.codexHotel.data.models.enums.RoomStatus;
import com.semicolon.codexHotel.data.models.enums.RoomType;
import com.semicolon.codexHotel.data.repositories.RoomRepository;
import com.semicolon.codexHotel.dtos.requests.AddRoomRequest;
import com.semicolon.codexHotel.dtos.responses.RoomResponse;
import com.semicolon.codexHotel.exceptions.RoomNotAvailableException;
import com.semicolon.codexHotel.exceptions.RoomNotFoundException;
import com.semicolon.codexHotel.services.RoomCounterService;
import com.semicolon.codexHotel.services.RoomService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RoomServiceTest {

    @Mock
    private RoomRepository roomRepository;

    @Mock
    private RoomCounterService roomCounterService;

    @InjectMocks
    private RoomService roomService;

    private Room buildRoom(String number, RoomType type, RoomStatus status) {
        Room room = new Room();
        room.setRoomNumber(number);
        room.setRoomType(type);
        room.setRoomStatus(status);
        return room;
    }

    // ── addRoom ───────────────────────────────────────────────────────────────

    @Test
    void addRoom_singleType_generatesRoomNumberAndPersists() {
        AddRoomRequest request = new AddRoomRequest();
        request.setRoomType(RoomType.SINGLE);

        when(roomCounterService.generateRoomNumber(RoomType.SINGLE)).thenReturn("SN-1");
        when(roomRepository.save(any(Room.class))).thenAnswer(inv -> inv.getArgument(0));

        RoomResponse response = roomService.addRoom(request);

        assertEquals("SN-1", response.getRoomNumber());
        assertEquals(RoomType.SINGLE, response.getRoomType());
        verify(roomRepository).save(any(Room.class));
    }

    @Test
    void addRoom_doubleType_generatesCorrectPrefix() {
        AddRoomRequest request = new AddRoomRequest();
        request.setRoomType(RoomType.DOUBLE);

        when(roomCounterService.generateRoomNumber(RoomType.DOUBLE)).thenReturn("DB-1");
        when(roomRepository.save(any(Room.class))).thenAnswer(inv -> inv.getArgument(0));

        RoomResponse response = roomService.addRoom(request);

        assertEquals("DB-1", response.getRoomNumber());
        assertEquals(RoomType.DOUBLE, response.getRoomType());
    }

    // ── getAllAvailableRooms ───────────────────────────────────────────────────

    @Test
    void getAllAvailableRooms_returnsOnlyAvailableRooms() {
        Room available = buildRoom("SN-1", RoomType.SINGLE, RoomStatus.AVAILABLE);

        when(roomRepository.findByRoomStatus(RoomStatus.AVAILABLE)).thenReturn(List.of(available));

        List<RoomResponse> responses = roomService.getAllAvailableRooms();

        assertEquals(1, responses.size());
        assertEquals("SN-1", responses.get(0).getRoomNumber());
        assertEquals(RoomStatus.AVAILABLE, responses.get(0).getRoomStatus());
    }

    @Test
    void getAllAvailableRooms_noRooms_returnsEmptyList() {
        when(roomRepository.findByRoomStatus(RoomStatus.AVAILABLE)).thenReturn(List.of());

        assertTrue(roomService.getAllAvailableRooms().isEmpty());
    }

    // ── getAllRooms ────────────────────────────────────────────────────────────

    @Test
    void getAllRooms_returnsAllRoomsRegardlessOfStatus() {
        List<Room> all = List.of(
                buildRoom("SN-1", RoomType.SINGLE, RoomStatus.AVAILABLE),
                buildRoom("DB-1", RoomType.DOUBLE, RoomStatus.OCCUPIED),
                buildRoom("ST-1", RoomType.SUITE,  RoomStatus.MAINTENANCE)
        );
        when(roomRepository.findAll()).thenReturn(all);

        List<RoomResponse> responses = roomService.getAllRooms();

        assertEquals(3, responses.size());
    }

    // ── markRoomForMaintenance ────────────────────────────────────────────────

    @Test
    void markRoomForMaintenance_existingRoom_setsStatusToMaintenance() {
        Room room = buildRoom("SN-1", RoomType.SINGLE, RoomStatus.AVAILABLE);

        when(roomRepository.findByRoomNumber("SN-1")).thenReturn(Optional.of(room));
        when(roomRepository.save(any(Room.class))).thenAnswer(inv -> inv.getArgument(0));

        RoomResponse response = roomService.markRoomForMaintenance("SN-1");

        assertEquals(RoomStatus.MAINTENANCE, response.getRoomStatus());
        verify(roomRepository).save(argThat(r -> r.getRoomStatus() == RoomStatus.MAINTENANCE));
    }

    @Test
    void markRoomForMaintenance_roomNotFound_throwsRoomNotFoundException() {
        when(roomRepository.findByRoomNumber("SN-999")).thenReturn(Optional.empty());

        assertThrows(RoomNotFoundException.class,
                () -> roomService.markRoomForMaintenance("SN-999"));
    }

    // ── markRoomAsAvailable ───────────────────────────────────────────────────

    @Test
    void markRoomAsAvailable_roomInMaintenance_setsStatusToAvailable() {
        Room room = buildRoom("SN-1", RoomType.SINGLE, RoomStatus.MAINTENANCE);

        when(roomRepository.findByRoomNumber("SN-1")).thenReturn(Optional.of(room));
        when(roomRepository.save(any(Room.class))).thenAnswer(inv -> inv.getArgument(0));

        RoomResponse response = roomService.markRoomAsAvailable("SN-1");

        assertEquals(RoomStatus.AVAILABLE, response.getRoomStatus());
    }

    @Test
    void markRoomAsAvailable_roomNotInMaintenance_throwsRoomNotAvailableException() {
        Room room = buildRoom("SN-1", RoomType.SINGLE, RoomStatus.AVAILABLE);

        when(roomRepository.findByRoomNumber("SN-1")).thenReturn(Optional.of(room));

        assertThrows(RoomNotAvailableException.class,
                () -> roomService.markRoomAsAvailable("SN-1"));
    }

    @Test
    void markRoomAsAvailable_roomNotFound_throwsRoomNotFoundException() {
        when(roomRepository.findByRoomNumber("SN-999")).thenReturn(Optional.empty());

        assertThrows(RoomNotFoundException.class,
                () -> roomService.markRoomAsAvailable("SN-999"));
    }
}