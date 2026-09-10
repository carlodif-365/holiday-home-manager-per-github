package com.example.holidayhome.controllers;

import com.example.holidayhome.entities.ExtraService;
import com.example.holidayhome.exceptions.ValidationException;
import com.example.holidayhome.payloads.extraservices.NewExtraServiceDTO;
import com.example.holidayhome.services.ExtraServiceService;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/extra-services")
public class ExtraServicesController {

    private final ExtraServiceService extraServiceService;

    public ExtraServicesController(ExtraServiceService extraServiceService) {
        this.extraServiceService = extraServiceService;
    }

    @GetMapping
    public List<ExtraService> getExtraServices() {
        return this.extraServiceService.findAll();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAuthority('ADMIN')")
    public ExtraService createExtraService(@Validated @RequestBody NewExtraServiceDTO body, BindingResult validationResult) {
        checkValidation(validationResult);
        return this.extraServiceService.create(body);
    }

    @DeleteMapping("/{extraServiceId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasAuthority('ADMIN')")
    public void deleteExtraService(@PathVariable UUID extraServiceId) {
        this.extraServiceService.delete(extraServiceId);
    }

    private void checkValidation(BindingResult validationResult) {
        if (validationResult.hasErrors()) {
            throw new ValidationException(validationResult.getFieldErrors().stream()
                    .map(DefaultMessageSourceResolvable::getDefaultMessage).toList());
        }
    }
}
