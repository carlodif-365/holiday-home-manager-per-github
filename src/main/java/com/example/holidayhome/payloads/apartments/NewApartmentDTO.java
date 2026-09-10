package com.example.holidayhome.payloads.apartments;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record NewApartmentDTO(
        @NotBlank(message = "Il codice appartamento e' obbligatorio") String code,
        @NotNull @Min(0) Integer floor,
        @NotNull @Min(1) Integer maxGuests,
        Double sizeSqm,
        String description,
        @NotNull @DecimalMin(value = "0.0", inclusive = false, message = "Il prezzo deve essere maggiore di zero") BigDecimal pricePerNight
) {
}
