package com.example.holidayhome.payloads.guests;

import com.example.holidayhome.entities.DocumentType;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;

import java.time.LocalDate;

public record NewGuestDTO(
        @NotBlank(message = "Il nome e' obbligatorio") String firstName,
        @NotBlank(message = "Il cognome e' obbligatorio") String lastName,
        @NotNull(message = "La data di nascita e' obbligatoria") @Past(message = "La data di nascita deve essere nel passato") LocalDate dateOfBirth,
        String placeOfBirth,
        @NotBlank(message = "La nazionalita' e' obbligatoria") String nationality,
        @NotNull(message = "Il tipo di documento e' obbligatorio") DocumentType documentType,
        @NotBlank(message = "Il numero di documento e' obbligatorio") String documentNumber,
        LocalDate documentExpiryDate,
        @NotBlank(message = "L'email e' obbligatoria") @Email(message = "Email non valida") String email,
        String phone,
        String address
) {
}
