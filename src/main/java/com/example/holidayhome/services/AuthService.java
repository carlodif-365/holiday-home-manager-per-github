package com.example.holidayhome.services;

import com.example.holidayhome.entities.User;
import com.example.holidayhome.exceptions.UnauthorizedException;
import com.example.holidayhome.payloads.users.LoginDTO;
import com.example.holidayhome.security.TokenTools;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UsersService usersService;
    private final TokenTools tokenTools;
    private final PasswordEncoder bcrypt;

    public AuthService(UsersService usersService, TokenTools tokenTools, PasswordEncoder bcrypt) {
        this.usersService = usersService;
        this.tokenTools = tokenTools;
        this.bcrypt = bcrypt;
    }

    public String checkCredentialsAndGenerateToken(LoginDTO body) {
        User found = this.usersService.findByEmail(body.email());

        if (bcrypt.matches(body.password(), found.getPassword())) {
            return this.tokenTools.generateToken(found);
        } else {
            throw new UnauthorizedException("Credenziali non valide");
        }
    }
}
