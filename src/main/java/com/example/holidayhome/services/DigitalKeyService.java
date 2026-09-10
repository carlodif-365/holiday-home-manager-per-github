package com.example.holidayhome.services;

import com.example.holidayhome.entities.*;
import com.example.holidayhome.exceptions.NotFoundException;
import com.example.holidayhome.repositories.ApartmentDoorRepository;
import com.example.holidayhome.repositories.DigitalKeyRepository;
import com.example.holidayhome.repositories.MainEntranceDoorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class DigitalKeyService {

    private static final LocalTime CHECK_IN_TIME = LocalTime.of(15, 0);
    private static final LocalTime CHECK_OUT_TIME = LocalTime.of(11, 0);

    @Autowired
    private DigitalKeyRepository digitalKeyRepository;
    @Autowired
    private MainEntranceDoorRepository mainEntranceDoorRepository;
    @Autowired
    private ApartmentDoorRepository apartmentDoorRepository;
    @Autowired
    private NotificationService notificationService;

    private final SecureRandom random = new SecureRandom();

    public List<DigitalKey> findByBooking(UUID bookingId) {
        return this.digitalKeyRepository.findByBookingId(bookingId);
    }

    /**
     * Genera le due chiavi digitali (portone + appartamento) necessarie per una prenotazione
     * al momento del check-in, e prova a notificarle via email all'ospite principale.
     */
    public List<DigitalKey> issueKeysForBooking(Booking booking) {
        MainEntranceDoor mainDoor = this.mainEntranceDoorRepository.findFirstByOrderByCreatedAtAsc()
                .orElseThrow(() -> new NotFoundException("Portone principale non configurato: eseguire il seed iniziale"));

        ApartmentDoor apartmentDoor = this.apartmentDoorRepository.findByApartmentId(booking.getApartment().getId())
                .orElseThrow(() -> new NotFoundException("Porta non trovata per l'appartamento " + booking.getApartment().getCode()));

        LocalDateTime validFrom = booking.getCheckInDate().atTime(CHECK_IN_TIME);
        LocalDateTime validTo = booking.getCheckOutDate().atTime(CHECK_OUT_TIME);

        List<DigitalKey> keys = new ArrayList<>();
        keys.add(generateKey(booking, mainDoor, validFrom, validTo));
        keys.add(generateKey(booking, apartmentDoor, validFrom, validTo));

        Guest mainGuest = booking.getGuests().stream()
                .filter(BookingGuest::isMainGuest)
                .findFirst()
                .map(BookingGuest::getGuest)
                .orElse(booking.getGuests().get(0).getGuest());

        String messageId = this.notificationService.sendDigitalKeysEmail(mainGuest, booking.getApartment().getCode(), keys);
        if (messageId != null) {
            keys.forEach(k -> k.setNotificationReference(messageId));
            this.digitalKeyRepository.saveAll(keys);
        }

        return keys;
    }

    public void revokeKeysForBooking(Booking booking) {
        List<DigitalKey> keys = this.digitalKeyRepository.findByBookingId(booking.getId());
        LocalDateTime now = LocalDateTime.now();
        keys.forEach(k -> {
            if (k.getStatus() == KeyStatus.ACTIVE) {
                k.setStatus(KeyStatus.REVOKED);
                k.setRevokedAt(now);
            }
        });
        this.digitalKeyRepository.saveAll(keys);
    }

    private DigitalKey generateKey(Booking booking, Door door, LocalDateTime validFrom, LocalDateTime validTo) {
        String code = generateNumericCode();
        DigitalKey key = new DigitalKey(booking, door, code, validFrom, validTo);
        return this.digitalKeyRepository.save(key);
    }

    private String generateNumericCode() {
        int code = 100000 + random.nextInt(900000); // 6 cifre
        return String.valueOf(code);
    }
}
