package com.example.holidayhome.payloads.bookings;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

/**
 * Il primo elemento di guestIds e' considerato l'ospite principale della prenotazione.
 * Gli ospiti vanno creati preventivamente con POST /guests.
 */
public record NewBookingDTO(
        @NotNull(message = "L'appartamento e' obbligatorio") UUID apartmentId,
        @NotNull(message = "La data di check-in e' obbligatoria") @FutureOrPresent(message = "Il check-in non puo' essere nel passato") LocalDate checkInDate,
        @NotNull(message = "La data di check-out e' obbligatoria") LocalDate checkOutDate,
        @NotNull @Min(1) Integer numberOfGuests,
        String notes,
        @NotEmpty(message = "Serve almeno un ospite") List<UUID> guestIds
) {
}
