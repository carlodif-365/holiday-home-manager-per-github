package com.example.holidayhome.payloads.apartments;

import com.example.holidayhome.entities.ApartmentStatus;
import jakarta.validation.constraints.NotNull;

public record UpdateApartmentStatusDTO(@NotNull ApartmentStatus status) {
}
