package com.example.holidayhome.payloads.keys;

import com.example.holidayhome.entities.ApartmentDoor;
import com.example.holidayhome.entities.DigitalKey;
import com.example.holidayhome.entities.KeyStatus;
import com.example.holidayhome.entities.MainEntranceDoor;

import java.time.LocalDateTime;
import java.util.UUID;

public record DigitalKeyResponseDTO(
        UUID id,
        String doorLabel,
        String code,
        LocalDateTime validFrom,
        LocalDateTime validTo,
        KeyStatus status
) {
    public static DigitalKeyResponseDTO from(DigitalKey key) {
        String label = switch (key.getDoor()) {
            case MainEntranceDoor mainDoor -> mainDoor.getLabel();
            case ApartmentDoor apartmentDoor -> "Appartamento " + apartmentDoor.getApartment().getCode();
            default -> "Porta";
        };
        return new DigitalKeyResponseDTO(key.getId(), label, key.getCode(), key.getValidFrom(), key.getValidTo(), key.getStatus());
    }
}
