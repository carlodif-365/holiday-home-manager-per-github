package com.example.holidayhome.repositories;

import com.example.holidayhome.entities.Apartment;
import com.example.holidayhome.entities.ApartmentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ApartmentRepository extends JpaRepository<Apartment, UUID> {

    Optional<Apartment> findByCode(String code);

    boolean existsByCode(String code);

    // Derived query con ordinamento
    List<Apartment> findByStatusOrderByPricePerNightAsc(ApartmentStatus status);

    // Filtro combinato: stato + capienza minima
    List<Apartment> findByStatusAndMaxGuestsGreaterThanEqual(ApartmentStatus status, Integer maxGuests);

    // JPQL: appartamenti liberi in un intervallo di date (nessuna prenotazione attiva sovrapposta)
    // e con capienza sufficiente -> query "reale" usata dalla ricerca disponibilita'
    @Query("""
            SELECT a FROM Apartment a
            WHERE a.status = com.example.holidayhome.entities.ApartmentStatus.AVAILABLE
            AND a.maxGuests >= :minGuests
            AND a.id NOT IN (
                SELECT b.apartment.id FROM Booking b
                WHERE b.status IN (com.example.holidayhome.entities.BookingStatus.CONFIRMED, com.example.holidayhome.entities.BookingStatus.CHECKED_IN)
                AND b.checkInDate < :checkOutDate
                AND b.checkOutDate > :checkInDate
            )
            ORDER BY a.pricePerNight ASC
            """)
    List<Apartment> findAvailableApartments(@Param("checkInDate") LocalDate checkInDate,
                                             @Param("checkOutDate") LocalDate checkOutDate,
                                             @Param("minGuests") Integer minGuests);

    // Query nativa di aggregazione: numero di appartamenti per stato
    @Query(value = "SELECT status, COUNT(*) as total FROM apartments GROUP BY status", nativeQuery = true)
    List<Object[]> countApartmentsByStatus();
}
