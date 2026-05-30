package com.semicolon.codexHotel.data.repositories;

import com.semicolon.codexHotel.data.models.Reservation;
import com.semicolon.codexHotel.data.models.enums.ReservationStatus;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface ReservationRepository extends MongoRepository<Reservation, String> {
    Optional<Reservation> findByReferenceNumber(String referenceNumber);
    List<Reservation>findByCheckInDateLessThanEqualAndCheckOutDateGreaterThanEqual(LocalDate checkInDate, LocalDate checkOutDate);
    List<Reservation> findByCheckInDateAndReservationStatus(LocalDate checkInDate, ReservationStatus status);

    List<Reservation> findByCheckOutDateAndReservationStatus(LocalDate checkOutDate, ReservationStatus status);
    List <Reservation> findByGuestId(String guestId);
}
