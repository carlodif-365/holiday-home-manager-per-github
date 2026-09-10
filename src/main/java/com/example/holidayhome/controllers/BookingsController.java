package com.example.holidayhome.controllers;

import com.example.holidayhome.entities.Booking;
import com.example.holidayhome.entities.BookingStatus;
import com.example.holidayhome.entities.User;
import com.example.holidayhome.exceptions.ValidationException;
import com.example.holidayhome.payloads.bookings.AddExtraServiceDTO;
import com.example.holidayhome.payloads.bookings.BookingResponseDTO;
import com.example.holidayhome.payloads.bookings.MonthlyRevenueDTO;
import com.example.holidayhome.payloads.bookings.NewBookingDTO;
import com.example.holidayhome.services.BookingService;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/bookings")
public class BookingsController {

    private final BookingService bookingService;

    public BookingsController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @GetMapping
    public List<BookingResponseDTO> getBookings() {
        return this.bookingService.findAll().stream().map(BookingResponseDTO::from).collect(Collectors.toList());
    }

    @GetMapping("/{bookingId}")
    public BookingResponseDTO getBooking(@PathVariable UUID bookingId) {
        return BookingResponseDTO.from(this.bookingService.findById(bookingId));
    }

    // GET /bookings/status/CHECKED_IN
    @GetMapping("/status/{status}")
    public List<BookingResponseDTO> getByStatus(@PathVariable BookingStatus status) {
        return this.bookingService.findByStatus(status).stream().map(BookingResponseDTO::from).collect(Collectors.toList());
    }

    @GetMapping("/today/arrivals")
    public List<BookingResponseDTO> getTodayArrivals() {
        return this.bookingService.findTodayArrivals().stream().map(BookingResponseDTO::from).collect(Collectors.toList());
    }

    @GetMapping("/today/departures")
    public List<BookingResponseDTO> getTodayDepartures() {
        return this.bookingService.findTodayDepartures().stream().map(BookingResponseDTO::from).collect(Collectors.toList());
    }

    @GetMapping("/stats/monthly-revenue")
    @PreAuthorize("hasAuthority('ADMIN')")
    public List<MonthlyRevenueDTO> getMonthlyRevenue() {
        return this.bookingService.monthlyRevenue();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BookingResponseDTO createBooking(@AuthenticationPrincipal User currentUser,
                                             @Validated @RequestBody NewBookingDTO body,
                                             BindingResult validationResult) {
        checkValidation(validationResult);
        Booking booking = this.bookingService.create(body, currentUser);
        return BookingResponseDTO.from(booking);
    }

    @PostMapping("/{bookingId}/check-in")
    public BookingResponseDTO checkIn(@PathVariable UUID bookingId) {
        return BookingResponseDTO.from(this.bookingService.checkIn(bookingId));
    }

    @PostMapping("/{bookingId}/check-out")
    public BookingResponseDTO checkOut(@PathVariable UUID bookingId) {
        return BookingResponseDTO.from(this.bookingService.checkOut(bookingId));
    }

    @PostMapping("/{bookingId}/cancel")
    public BookingResponseDTO cancel(@PathVariable UUID bookingId) {
        return BookingResponseDTO.from(this.bookingService.cancel(bookingId));
    }

    @PostMapping("/{bookingId}/extra-services")
    public BookingResponseDTO addExtraService(@PathVariable UUID bookingId, @Validated @RequestBody AddExtraServiceDTO body, BindingResult validationResult) {
        checkValidation(validationResult);
        return BookingResponseDTO.from(this.bookingService.addExtraService(bookingId, body));
    }

    private void checkValidation(BindingResult validationResult) {
        if (validationResult.hasErrors()) {
            throw new ValidationException(validationResult.getFieldErrors().stream()
                    .map(DefaultMessageSourceResolvable::getDefaultMessage).toList());
        }
    }
}
