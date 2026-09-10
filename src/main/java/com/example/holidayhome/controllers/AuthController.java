package com.example.holidayhome.controllers;

import com.example.holidayhome.exceptions.ValidationException;
import com.example.holidayhome.payloads.users.LoginDTO;
import com.example.holidayhome.payloads.users.LoginRespDTO;
import com.example.holidayhome.payloads.users.NewUserDTO;
import com.example.holidayhome.payloads.users.NewUserResponseDTO;
import com.example.holidayhome.services.AuthService;
import com.example.holidayhome.services.UsersService;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;
    private final UsersService usersService;

    public AuthController(AuthService authService, UsersService usersService) {
        this.authService = authService;
        this.usersService = usersService;
    }

    @PostMapping("/login")
    public LoginRespDTO login(@Validated @RequestBody LoginDTO payload, BindingResult validationResult) {
        checkValidation(validationResult);
        return new LoginRespDTO(this.authService.checkCredentialsAndGenerateToken(payload));
    }

    // Registrazione dello staff (ADMIN/RECEPTIONIST). In un ambiente di produzione andrebbe
    // riservata a un ADMIN gia' autenticato o protetta da un setup iniziale: qui resta aperta
    // per semplicita' e per permettere di creare il primo utente.
    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public NewUserResponseDTO register(@Validated @RequestBody NewUserDTO body, BindingResult validationResult) {
        checkValidation(validationResult);
        return this.usersService.saveUser(body);
    }

    private void checkValidation(BindingResult validationResult) {
        if (validationResult.hasErrors()) {
            throw new ValidationException(validationResult.getFieldErrors().stream()
                    .map(DefaultMessageSourceResolvable::getDefaultMessage).toList());
        }
    }
}
