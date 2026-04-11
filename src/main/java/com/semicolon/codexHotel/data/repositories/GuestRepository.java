package com.semicolon.codexHotel.data.repositories;

import com.semicolon.codexHotel.data.models.Guest;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface GuestRepository extends MongoRepository<Guest, String> {
    Optional<Guest> findByEmail(String email);
    Optional<Guest> findByGuestReferenceNumber(String guestReferenceNumber);
}
