package com.example.holidayhome.repositories;

import com.example.holidayhome.entities.Booking;
import com.example.holidayhome.entities.BookingStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Repository
public interface BookingRepository extends JpaRepository<Booking, UUID> {

    List<Booking> findByStatus(BookingStatus status);

    List<Booking> findByApartmentIdOrderByCheckInDateDesc(UUID apartmentId);

    // Arrivi/partenze del giorno (filtro combinato data + stato)
    List<Booking> findByCheckInDateAndStatus(LocalDate checkInDate, BookingStatus status);

    List<Booking> findByCheckOutDateAndStatus(LocalDate checkOutDate, BookingStatus status);

    // JPQL: verifica sovrapposizione per un dato appartamento, escludendo eventualmente una prenotazione
    // (usata in fase di creazione/modifica per prevenire doppie prenotazioni)
    @Query("""
            SELECT b FROM Booking b
            WHERE b.apartment.id = :apartmentId
            AND b.status IN (com.example.holidayhome.entities.BookingStatus.CONFIRMED, com.example.holidayhome.entities.BookingStatus.CHECKED_IN)
            AND b.checkInDate < :checkOutDate
            AND b.checkOutDate > :checkInDate
            AND (:excludeBookingId IS NULL OR b.id <> :excludeBookingId)
            """)
    List<Booking> findOverlappingBookings(@Param("apartmentId") UUID apartmentId,
                                           @Param("checkInDate") LocalDate checkInDate,
                                           @Param("checkOutDate") LocalDate checkOutDate,
                                           @Param("excludeBookingId") UUID excludeBookingId);

    // Aggregazione nativa: ricavi totali per mese (combina filtro per stato + raggruppamento)
    @Query(value = """
            SELECT to_char(check_in_date, 'YYYY-MM') AS month, SUM(total_price) AS revenue, COUNT(*) AS bookings_count
            FROM bookings
            WHERE status <> 'CANCELLED'
            GROUP BY to_char(check_in_date, 'YYYY-MM')
            ORDER BY month
            """, nativeQuery = true)
    List<Object[]> findMonthlyRevenue();

    long countByStatus(BookingStatus status);

    @Query("SELECT COALESCE(SUM(b.totalPrice), 0) FROM Booking b WHERE b.status = com.example.holidayhome.entities.BookingStatus.CHECKED_OUT")
    BigDecimal sumRevenueFromCompletedStays();
}
