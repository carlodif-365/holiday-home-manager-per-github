package com.example.holidayhome.exceptions;

import java.util.UUID;

public class NotFoundException extends RuntimeException {

    public NotFoundException(String message) {
        super(message);
    }

    public NotFoundException(UUID id) {
        super("Nessuna risorsa trovata con id " + id);
    }
}
