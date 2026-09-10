package com.example.holidayhome.repositories;

import com.example.holidayhome.entities.ExtraService;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ExtraServiceRepository extends JpaRepository<ExtraService, UUID> {

    Optional<ExtraService> findByName(String name);
}
