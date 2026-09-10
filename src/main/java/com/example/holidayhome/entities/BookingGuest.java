package com.example.holidayhome.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;

import java.util.UUID;

/**
 * Entita' ponte tra Booking e Guest: una prenotazione puo' avere piu' ospiti
 * (tutti da registrare) e uno di essi e' l'ospite principale (isMainGuest).
 */
@Entity
@Table(name = "booking_guests", uniqueConstraints = @UniqueConstraint(columnNames = {"booking_id", "guest_id"}))
public class BookingGuest {

    @Id
    @GeneratedValue
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "booking_id", nullable = false)
    @JsonBackReference
    private Booking booking;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "guest_id", nullable = false)
    private Guest guest;

    @Column(nullable = false)
    private boolean isMainGuest;

    public BookingGuest() {
    }

    public BookingGuest(Booking booking, Guest guest, boolean isMainGuest) {
        this.booking = booking;
        this.guest = guest;
        this.isMainGuest = isMainGuest;
    }

    public UUID getId() {
        return id;
    }

    public Booking getBooking() {
        return booking;
    }

    public Guest getGuest() {
        return guest;
    }

    public boolean isMainGuest() {
        return isMainGuest;
    }

    public void setMainGuest(boolean mainGuest) {
        isMainGuest = mainGuest;
    }
}
