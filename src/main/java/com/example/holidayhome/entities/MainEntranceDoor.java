package com.example.holidayhome.entities;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

/**
 * Il portone di ingresso della palazzina: condiviso da tutti gli appartamenti.
 * Normalmente esiste una sola riga di questo tipo per la struttura.
 */
@Entity
@DiscriminatorValue("main_entrance")
public class MainEntranceDoor extends Door {

    private String label; // es. "Portone principale - Via Roma 12"

    public MainEntranceDoor() {
    }

    public MainEntranceDoor(String lockIdentifier, String label) {
        super(lockIdentifier);
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }
}
