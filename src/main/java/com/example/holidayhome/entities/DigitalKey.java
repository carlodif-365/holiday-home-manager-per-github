package com.example.holidayhome.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Chiave digitale che apre UNA porta (portone o appartamento) per la durata
 * di UNA prenotazione. Ogni prenotazione genera due DigitalKey: una per il
 * portone principale e una per la porta dell'appartamento assegnato.
 */
@Entity
@Table(name = "digital_keys", uniqueConstraints = @UniqueConstraint(columnNames = {"booking_id", "door_id"}))
public class DigitalKey {

    @Id
    @GeneratedValue
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "booking_id", nullable = false)
    @JsonBackReference
    private Booking booking;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "door_id", nullable = false)
    private Door door;

    @Column(nullable = false, unique = true)
    private String code;

    @Column(nullable = false)
    private LocalDateTime validFrom;

    @Column(nullable = false)
    private LocalDateTime validTo;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private KeyStatus status;

    @Column(nullable = false, updatable = false)
    private LocalDateTime issuedAt;

    private LocalDateTime revokedAt;

    // Riferimento al messaggio restituito dal provider email/SMS (Brevo) quando
    // il codice viene notificato all'ospite: dimostra l'uso concreto della risposta
    // dell'API di terze parti all'interno della logica applicativa.
    private String notificationReference;

    public DigitalKey() {
    }

    public DigitalKey(Booking booking, Door door, String code, LocalDateTime validFrom, LocalDateTime validTo) {
        this.booking = booking;
        this.door = door;
        this.code = code;
        this.validFrom = validFrom;
        this.validTo = validTo;
        this.status = KeyStatus.ACTIVE;
        this.issuedAt = LocalDateTime.now();
    }

    public UUID getId() {
        return id;
    }

    public Booking getBooking() {
        return booking;
    }

    public Door getDoor() {
        return door;
    }

    public String getCode() {
        return code;
    }

    public LocalDateTime getValidFrom() {
        return validFrom;
    }

    public LocalDateTime getValidTo() {
        return validTo;
    }

    public KeyStatus getStatus() {
        return status;
    }

    public void setStatus(KeyStatus status) {
        this.status = status;
    }

    public LocalDateTime getIssuedAt() {
        return issuedAt;
    }

    public LocalDateTime getRevokedAt() {
        return revokedAt;
    }

    public void setRevokedAt(LocalDateTime revokedAt) {
        this.revokedAt = revokedAt;
    }

    public String getNotificationReference() {
        return notificationReference;
    }

    public void setNotificationReference(String notificationReference) {
        this.notificationReference = notificationReference;
    }
}
