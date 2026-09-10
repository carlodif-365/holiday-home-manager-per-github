package com.example.holidayhome.controllers;

import com.example.holidayhome.entities.Apartment;
import com.example.holidayhome.exceptions.ValidationException;
import com.example.holidayhome.payloads.apartments.ApartmentResponseDTO;
import com.example.holidayhome.payloads.apartments.NewApartmentDTO;
import com.example.holidayhome.payloads.apartments.UpdateApartmentStatusDTO;
import com.example.holidayhome.services.ApartmentService;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/apartments")
public class ApartmentsController {

    private final ApartmentService apartmentService;

    public ApartmentsController(ApartmentService apartmentService) {
        this.apartmentService = apartmentService;
    }

    @GetMapping
    public List<ApartmentResponseDTO> getApartments() {
        return this.apartmentService.findAll().stream().map(ApartmentResponseDTO::from).collect(Collectors.toList());
    }

    // GET /apartments/available?checkInDate=2026-07-01&checkOutDate=2026-07-08&minGuests=2
    @GetMapping("/available")
    public List<ApartmentResponseDTO> getAvailableApartments(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate checkInDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate checkOutDate,
            @RequestParam(required = false) Integer minGuests) {
        return this.apartmentService.findAvailable(checkInDate, checkOutDate, minGuests).stream()
                .map(ApartmentResponseDTO::from).collect(Collectors.toList());
    }

    @GetMapping("/{apartmentId}")
    public ApartmentResponseDTO getApartment(@PathVariable UUID apartmentId) {
        return ApartmentResponseDTO.from(this.apartmentService.findById(apartmentId));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAuthority('ADMIN')")
    public ApartmentResponseDTO createApartment(@Validated @RequestBody NewApartmentDTO body, BindingResult validationResult) {
        checkValidation(validationResult);
        return ApartmentResponseDTO.from(this.apartmentService.create(body));
    }

    @PutMapping("/{apartmentId}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ApartmentResponseDTO updateApartment(@PathVariable UUID apartmentId, @Validated @RequestBody NewApartmentDTO body, BindingResult validationResult) {
        checkValidation(validationResult);
        return ApartmentResponseDTO.from(this.apartmentService.update(apartmentId, body));
    }

    @PatchMapping("/{apartmentId}/status")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'RECEPTIONIST')")
    public ApartmentResponseDTO updateStatus(@PathVariable UUID apartmentId, @Validated @RequestBody UpdateApartmentStatusDTO body, BindingResult validationResult) {
        checkValidation(validationResult);
        return ApartmentResponseDTO.from(this.apartmentService.updateStatus(apartmentId, body.status()));
    }

    @PatchMapping("/{apartmentId}/photos")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ApartmentResponseDTO uploadPhoto(@PathVariable UUID apartmentId,
                                             @RequestParam("photo") MultipartFile file,
                                             @RequestParam(required = false) String caption) throws IOException {
        return ApartmentResponseDTO.from(this.apartmentService.uploadPhoto(apartmentId, file, caption));
    }

    @DeleteMapping("/{apartmentId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasAuthority('ADMIN')")
    public void deleteApartment(@PathVariable UUID apartmentId) {
        this.apartmentService.delete(apartmentId);
    }

    private void checkValidation(BindingResult validationResult) {
        if (validationResult.hasErrors()) {
            throw new ValidationException(validationResult.getFieldErrors().stream()
                    .map(DefaultMessageSourceResolvable::getDefaultMessage).toList());
        }
    }
}
