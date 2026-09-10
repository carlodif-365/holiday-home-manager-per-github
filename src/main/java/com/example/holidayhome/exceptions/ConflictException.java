package com.example.holidayhome.exceptions;

/**
 * Usata ad esempio quando un appartamento non e' disponibile nelle date richieste
 * o quando si tenta un'azione incompatibile con lo stato corrente di una risorsa
 * (es. check-in su una prenotazione gia' chiusa).
 */
public class ConflictException extends RuntimeException {

    public ConflictException(String message) {
        super(message);
    }
}
