package com.example.holidayhome.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;

/**
 * La porta d'ingresso di un singolo appartamento.
 */
@Entity
@DiscriminatorValue("apartment_door")
public class ApartmentDoor extends Door {

    @OneToOne(fetch = jakarta.persistence.FetchType.LAZY)
    @JoinColumn(name = "apartment_id", nullable = false, unique = true)
    @JsonIgnore
    private Apartment apartment;

    public ApartmentDoor() {
    }

    public ApartmentDoor(String lockIdentifier, Apartment apartment) {
        super(lockIdentifier);
        this.apartment = apartment;
    }

    public Apartment getApartment() {
        return apartment;
    }

    public void setApartment(Apartment apartment) {
        this.apartment = apartment;
    }
}
