package com.example.holidayhome.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Entita' ponte tra Booking e ExtraService (relazione N:N con attributi propri:
 * quantita' e prezzo unitario "fotografato" al momento della richiesta).
 */
@Entity
@Table(name = "booking_extra_services")
public class BookingExtraService {

    @Id
    @GeneratedValue
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "booking_id", nullable = false)
    @JsonBackReference
    private Booking booking;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "extra_service_id", nullable = false)
    private ExtraService extraService;

    @Column(nullable = false)
    private Integer quantity;

    @Column(nullable = false)
    private BigDecimal unitPrice;

    public BookingExtraService() {
    }

    public BookingExtraService(Booking booking, ExtraService extraService, Integer quantity, BigDecimal unitPrice) {
        this.booking = booking;
        this.extraService = extraService;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
    }

    public UUID getId() {
        return id;
    }

    public Booking getBooking() {
        return booking;
    }

    public ExtraService getExtraService() {
        return extraService;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(BigDecimal unitPrice) {
        this.unitPrice = unitPrice;
    }

    public BigDecimal getLineTotal() {
        return unitPrice.multiply(BigDecimal.valueOf(quantity));
    }
}
