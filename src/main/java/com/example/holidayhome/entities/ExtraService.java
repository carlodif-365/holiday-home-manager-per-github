package com.example.holidayhome.entities;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Servizio extra offerto dalla struttura (pulizie extra, transfer aeroporto, colazione...).
 * Si chiama "ExtraService" (e non "Service") per non entrare in conflitto con
 * l'annotazione Spring @Service.
 */
@Entity
@Table(name = "extra_services")
public class ExtraService {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(nullable = false, unique = true)
    private String name;

    private String description;

    @Column(nullable = false)
    private BigDecimal price;

    public ExtraService() {
    }

    public ExtraService(String name, String description, BigDecimal price) {
        this.name = name;
        this.description = description;
        this.price = price;
    }

    public UUID getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }
}
