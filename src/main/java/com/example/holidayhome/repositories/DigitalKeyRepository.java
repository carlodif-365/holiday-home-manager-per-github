package com.example.holidayhome.repositories;

import com.example.holidayhome.entities.DigitalKey;
import com.example.holidayhome.entities.KeyStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface DigitalKeyRepository extends JpaRepository<DigitalKey, UUID> {

    List<DigitalKey> findByBookingId(UUID bookingId);

    Optional<DigitalKey> findByCode(String code);

    List<DigitalKey> findByStatusAndValidToBefore(KeyStatus status, LocalDateTime moment);
}
