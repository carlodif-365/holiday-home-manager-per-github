package com.example.holidayhome.payloads.users;

import com.example.holidayhome.entities.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record NewUserDTO(
        @NotBlank(message = "Il nome e' obbligatorio") String firstName,
        @NotBlank(message = "Il cognome e' obbligatorio") String lastName,
        @NotBlank(message = "L'email e' obbligatoria") @Email(message = "Email non valida") String email,
        @NotBlank(message = "La password e' obbligatoria") @Size(min = 6, message = "La password deve avere almeno 6 caratteri") String password,
        @NotNull(message = "Il ruolo e' obbligatorio") Role role,
        String phone
) {
}
