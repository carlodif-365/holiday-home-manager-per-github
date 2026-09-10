package com.example.holidayhome.controllers;

import com.example.holidayhome.entities.User;
import com.example.holidayhome.exceptions.ValidationException;
import com.example.holidayhome.payloads.users.NewUserDTO;
import com.example.holidayhome.services.UsersService;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/users")
public class UsersController {

    private final UsersService usersService;

    public UsersController(UsersService usersService) {
        this.usersService = usersService;
    }

    @GetMapping
    @PreAuthorize("hasAnyAuthority('ADMIN', 'RECEPTIONIST')")
    public List<User> getUsers() {
        return this.usersService.findAll();
    }

    @GetMapping("/me")
    public User getOwnProfile(@AuthenticationPrincipal User currentUser) {
        return currentUser;
    }

    @PutMapping("/me")
    public User updateOwnProfile(@AuthenticationPrincipal User currentUser, @Validated @RequestBody NewUserDTO body, BindingResult validationResult) {
        checkValidation(validationResult);
        return this.usersService.findByIdAndUpdate(currentUser.getId(), body);
    }

    @PatchMapping("/me/avatar")
    public User uploadOwnAvatar(@AuthenticationPrincipal User currentUser, @RequestParam("avatar") MultipartFile file) throws IOException {
        return this.usersService.uploadAvatar(currentUser.getId(), file);
    }

    @PutMapping("/{userId}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public User updateUser(@PathVariable UUID userId, @Validated @RequestBody NewUserDTO body, BindingResult validationResult) {
        checkValidation(validationResult);
        return this.usersService.findByIdAndUpdate(userId, body);
    }

    @DeleteMapping("/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasAuthority('ADMIN')")
    public void deleteUser(@PathVariable UUID userId) {
        this.usersService.findByIdAndDelete(userId);
    }

    private void checkValidation(BindingResult validationResult) {
        if (validationResult.hasErrors()) {
            throw new ValidationException(validationResult.getFieldErrors().stream()
                    .map(DefaultMessageSourceResolvable::getDefaultMessage).toList());
        }
    }
}
