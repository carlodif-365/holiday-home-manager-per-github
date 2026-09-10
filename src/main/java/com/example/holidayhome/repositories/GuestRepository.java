package com.example.holidayhome.repositories;

import com.example.holidayhome.entities.Guest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface GuestRepository extends JpaRepository<Guest, UUID> {

    Optional<Guest> findByDocumentNumber(String documentNumber);

    boolean existsByDocumentNumber(String documentNumber);

    // Ricerca per cognome, case-insensitive, ordinata
    List<Guest> findByLastNameContainingIgnoreCaseOrderByLastNameAsc(String lastName);

    // JPQL: ospiti filtrati per nazionalita' e con documento in scadenza entro N giorni
    @Query("SELECT g FROM Guest g WHERE g.nationality = :nationality AND g.documentExpiryDate <= :expiryThreshold")
    List<Guest> findByNationalityWithExpiringDocument(String nationality, java.time.LocalDate expiryThreshold);
}
