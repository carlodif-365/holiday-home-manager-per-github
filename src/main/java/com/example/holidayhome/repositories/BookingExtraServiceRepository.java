package com.example.holidayhome.repositories;

import com.example.holidayhome.entities.BookingExtraService;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface BookingExtraServiceRepository extends JpaRepository<BookingExtraService, UUID> {

    List<BookingExtraService> findByBookingId(UUID bookingId);
}
