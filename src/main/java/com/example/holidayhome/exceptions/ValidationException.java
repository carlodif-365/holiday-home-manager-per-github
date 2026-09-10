package com.example.holidayhome.exceptions;

import java.util.List;

public class ValidationException extends RuntimeException {

    private final List<String> errorsList;

    public ValidationException(String message) {
        super(message);
        this.errorsList = List.of(message);
    }

    public ValidationException(List<String> errorsList) {
        super("Payload non valido");
        this.errorsList = errorsList;
    }

    public List<String> getErrorsList() {
        return errorsList;
    }
}
