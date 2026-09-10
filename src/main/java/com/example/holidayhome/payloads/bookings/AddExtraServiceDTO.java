package com.example.holidayhome.payloads.bookings;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record AddExtraServiceDTO(
        @NotNull UUID extraServiceId,
        @NotNull @Min(1) Integer quantity
) {
}
