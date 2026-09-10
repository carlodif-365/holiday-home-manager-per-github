package com.example.holidayhome.repositories;

import com.example.holidayhome.entities.Door;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface DoorRepository extends JpaRepository<Door, UUID> {

    Optional<Door> findByLockIdentifier(String lockIdentifier);
}
