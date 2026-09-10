package com.example.holidayhome.payloads.apartments;

import com.example.holidayhome.entities.Apartment;
import com.example.holidayhome.entities.ApartmentStatus;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public record ApartmentResponseDTO(
        UUID id,
        String code,
        Integer floor,
        Integer maxGuests,
        Double sizeSqm,
        String description,
        BigDecimal pricePerNight,
        ApartmentStatus status,
        List<String> photoUrls
) {
    public static ApartmentResponseDTO from(Apartment apartment) {
        List<String> photos = apartment.getPhotos().stream()
                .map(p -> p.getImageURL())
                .collect(Collectors.toList());
        return new ApartmentResponseDTO(
                apartment.getId(),
                apartment.getCode(),
                apartment.getFloor(),
                apartment.getMaxGuests(),
                apartment.getSizeSqm(),
                apartment.getDescription(),
                apartment.getPricePerNight(),
                apartment.getStatus(),
                photos
        );
    }
}
