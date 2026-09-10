package com.example.holidayhome.controllers;

import com.example.holidayhome.entities.Guest;
import com.example.holidayhome.exceptions.ValidationException;
import com.example.holidayhome.payloads.guests.NewGuestDTO;
import com.example.holidayhome.services.GuestService;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/guests")
public class GuestsController {

    private final GuestService guestService;

    public GuestsController(GuestService guestService) {
        this.guestService = guestService;
    }

    @GetMapping
    public List<Guest> getGuests() {
        return this.guestService.findAll();
    }

    // GET /guests/search?lastName=rossi
    @GetMapping("/search")
    public List<Guest> search(@RequestParam String lastName) {
        return this.guestService.searchByLastName(lastName);
    }

    @GetMapping("/{guestId}")
    public Guest getGuest(@PathVariable UUID guestId) {
        return this.guestService.findById(guestId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Guest createGuest(@Validated @RequestBody NewGuestDTO body, BindingResult validationResult) {
        checkValidation(validationResult);
        return this.guestService.create(body);
    }

    @PutMapping("/{guestId}")
    public Guest updateGuest(@PathVariable UUID guestId, @Validated @RequestBody NewGuestDTO body, BindingResult validationResult) {
        checkValidation(validationResult);
        return this.guestService.update(guestId, body);
    }

    @DeleteMapping("/{guestId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasAnyAuthority('ADMIN', 'RECEPTIONIST')")
    public void deleteGuest(@PathVariable UUID guestId) {
        this.guestService.delete(guestId);
    }

    private void checkValidation(BindingResult validationResult) {
        if (validationResult.hasErrors()) {
            throw new ValidationException(validationResult.getFieldErrors().stream()
                    .map(DefaultMessageSourceResolvable::getDefaultMessage).toList());
        }
    }
}
