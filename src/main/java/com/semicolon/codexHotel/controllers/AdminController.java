package com.semicolon.codexHotel.controllers;

import com.semicolon.codexHotel.dtos.requests.AddRoomRequest;
import com.semicolon.codexHotel.dtos.requests.CreateFrontDeskRequest;
import com.semicolon.codexHotel.dtos.requests.LoginRequest;
import com.semicolon.codexHotel.dtos.responses.*;
import com.semicolon.codexHotel.services.AdminService;
import com.semicolon.codexHotel.services.ReportService;
import com.semicolon.codexHotel.services.RoomService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {
    private final AdminService adminService;
    private final RoomService roomService;
    private final ReportService reportService;

    @PostMapping("/login")
    public ResponseEntity<AdminLoginResponse> login(@Valid @RequestBody LoginRequest request){
        return ResponseEntity.status(HttpStatus.OK).body(adminService.login(request));
    }

    @PostMapping("/frontdesk")
    public ResponseEntity<FrontDeskLoginResponse> createFrontDesk(@Valid @RequestBody CreateFrontDeskRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(adminService.createFrontDesk(request));
    }

    @PostMapping("/rooms")
    public ResponseEntity<RoomResponse> addRoom(@Valid @RequestBody AddRoomRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(roomService.addRoom(request));
    }

    @GetMapping("/rooms")
    public ResponseEntity<List<RoomResponse>> getAllRooms() {
        return ResponseEntity.status(HttpStatus.OK).body(roomService.getAllRooms());
    }

    @PatchMapping("/rooms/{roomNumber}/maintenance")
    public ResponseEntity<RoomResponse> markRoomForMaintenance(@PathVariable String roomNumber) {
        return ResponseEntity.status(HttpStatus.OK).body(roomService.markRoomForMaintenance(roomNumber));
    }

    @GetMapping("/reports")
    public ResponseEntity<ReportResponse> generateReport() {
        return ResponseEntity.status(HttpStatus.OK).body(reportService.generateReport());
    }
}
