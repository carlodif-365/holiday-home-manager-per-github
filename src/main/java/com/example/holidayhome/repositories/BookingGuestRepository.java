package com.example.holidayhome.repositories;

import com.example.holidayhome.entities.BookingGuest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface BookingGuestRepository extends JpaRepository<BookingGuest, UUID> {

    List<BookingGuest> findByBookingId(UUID bookingId);

    List<BookingGuest> findByGuestId(UUID guestId);
}
