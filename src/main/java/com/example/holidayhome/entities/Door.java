package com.example.holidayhome.entities;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Gerarchia di ereditarieta' richiesta dal dominio: ogni porta della struttura
 * (il portone principale condiviso oppure la porta di un singolo appartamento)
 * condivide gli stessi attributi di base (id, codice serratura, data creazione)
 * ma si specializza in due sotto-tipi con caratteristiche diverse.
 * Strategia JOINED: una tabella "doors" con i campi comuni + una tabella per sotto-tipo.
 */
@Entity
@Table(name = "doors")
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(name = "door_type")
public abstract class Door {

    @Id
    @GeneratedValue
    protected UUID id;

    @Column(nullable = false, unique = true)
    protected String lockIdentifier;

    @Column(nullable = false, updatable = false)
    protected LocalDateTime createdAt;

    public Door() {
    }

    public Door(String lockIdentifier) {
        this.lockIdentifier = lockIdentifier;
        this.createdAt = LocalDateTime.now();
    }

    public UUID getId() {
        return id;
    }

    public String getLockIdentifier() {
        return lockIdentifier;
    }

    public void setLockIdentifier(String lockIdentifier) {
        this.lockIdentifier = lockIdentifier;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
