package com.example.holidayhome.repositories;

import com.example.holidayhome.entities.ApartmentDoor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ApartmentDoorRepository extends JpaRepository<ApartmentDoor, UUID> {

    Optional<ApartmentDoor> findByApartmentId(UUID apartmentId);
}
