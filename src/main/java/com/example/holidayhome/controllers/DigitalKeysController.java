package com.example.holidayhome.controllers;

import com.example.holidayhome.payloads.keys.DigitalKeyResponseDTO;
import com.example.holidayhome.services.DigitalKeyService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/digital-keys")
public class DigitalKeysController {

    private final DigitalKeyService digitalKeyService;

    public DigitalKeysController(DigitalKeyService digitalKeyService) {
        this.digitalKeyService = digitalKeyService;
    }

    // GET /digital-keys/booking/{bookingId}
    @GetMapping("/booking/{bookingId}")
    public List<DigitalKeyResponseDTO> getKeysForBooking(@PathVariable UUID bookingId) {
        return this.digitalKeyService.findByBooking(bookingId).stream()
                .map(DigitalKeyResponseDTO::from).collect(Collectors.toList());
    }
}
