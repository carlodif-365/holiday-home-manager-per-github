package com.example.holidayhome.payloads.bookings;

import com.example.holidayhome.entities.Booking;
import com.example.holidayhome.entities.BookingStatus;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public record BookingResponseDTO(
        UUID id,
        String apartmentCode,
        LocalDate checkInDate,
        LocalDate checkOutDate,
        Integer numberOfGuests,
        BookingStatus status,
        BigDecimal totalPrice,
        String notes,
        List<String> guestNames,
        LocalDateTime createdAt
) {
    public static BookingResponseDTO from(Booking booking) {
        List<String> names = booking.getGuests().stream()
                .map(bg -> bg.getGuest().getFirstName() + " " + bg.getGuest().getLastName() + (bg.isMainGuest() ? " (principale)" : ""))
                .collect(Collectors.toList());
        return new BookingResponseDTO(
                booking.getId(),
                booking.getApartment().getCode(),
                booking.getCheckInDate(),
                booking.getCheckOutDate(),
                booking.getNumberOfGuests(),
                booking.getStatus(),
                booking.getTotalPrice(),
                booking.getNotes(),
                names,
                booking.getCreatedAt()
        );
    }
}
