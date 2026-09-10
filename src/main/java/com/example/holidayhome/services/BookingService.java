package com.example.holidayhome.services;

import com.example.holidayhome.entities.*;
import com.example.holidayhome.exceptions.ConflictException;
import com.example.holidayhome.exceptions.NotFoundException;
import com.example.holidayhome.exceptions.ValidationException;
import com.example.holidayhome.payloads.bookings.AddExtraServiceDTO;
import com.example.holidayhome.payloads.bookings.MonthlyRevenueDTO;
import com.example.holidayhome.payloads.bookings.NewBookingDTO;
import com.example.holidayhome.repositories.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class BookingService {

    @Autowired
    private BookingRepository bookingRepository;
    @Autowired
    private ApartmentRepository apartmentRepository;
    @Autowired
    private GuestRepository guestRepository;
    @Autowired
    private ExtraServiceRepository extraServiceRepository;
    @Autowired
    private BookingExtraServiceRepository bookingExtraServiceRepository;
    @Autowired
    private DigitalKeyService digitalKeyService;

    public List<Booking> findAll() {
        return this.bookingRepository.findAll();
    }

    public Booking findById(UUID bookingId) {
        return this.bookingRepository.findById(bookingId).orElseThrow(() -> new NotFoundException(bookingId));
    }

    public List<Booking> findByStatus(BookingStatus status) {
        return this.bookingRepository.findByStatus(status);
    }

    public List<Booking> findTodayArrivals() {
        return this.bookingRepository.findByCheckInDateAndStatus(LocalDate.now(), BookingStatus.CONFIRMED);
    }

    public List<Booking> findTodayDepartures() {
        return this.bookingRepository.findByCheckOutDateAndStatus(LocalDate.now(), BookingStatus.CHECKED_IN);
    }

    public List<MonthlyRevenueDTO> monthlyRevenue() {
        return this.bookingRepository.findMonthlyRevenue().stream()
                .map(row -> new MonthlyRevenueDTO((String) row[0], (BigDecimal) row[1], ((Number) row[2]).longValue()))
                .collect(Collectors.toList());
    }

    public Booking create(NewBookingDTO body, User staff) {
        if (!body.checkOutDate().isAfter(body.checkInDate())) {
            throw new ValidationException("Il check-out deve essere successivo al check-in");
        }

        Apartment apartment = this.apartmentRepository.findById(body.apartmentId())
                .orElseThrow(() -> new NotFoundException(body.apartmentId()));

        if (apartment.getStatus() == ApartmentStatus.OUT_OF_SERVICE) {
            throw new ConflictException("L'appartamento " + apartment.getCode() + " non e' prenotabile");
        }

        if (body.numberOfGuests() > apartment.getMaxGuests()) {
            throw new ValidationException("L'appartamento " + apartment.getCode() + " ospita al massimo " + apartment.getMaxGuests() + " persone");
        }

        List<Booking> overlapping = this.bookingRepository.findOverlappingBookings(
                apartment.getId(), body.checkInDate(), body.checkOutDate(), null);
        if (!overlapping.isEmpty()) {
            throw new ConflictException("L'appartamento " + apartment.getCode() + " non e' disponibile nelle date richieste");
        }

        long nights = ChronoUnit.DAYS.between(body.checkInDate(), body.checkOutDate());
        BigDecimal totalPrice = apartment.getPricePerNight().multiply(BigDecimal.valueOf(nights));

        Booking booking = new Booking(apartment, body.checkInDate(), body.checkOutDate(), body.numberOfGuests(), totalPrice, body.notes(), staff);

        for (int i = 0; i < body.guestIds().size(); i++) {
            UUID guestId = body.guestIds().get(i);
            Guest guest = this.guestRepository.findById(guestId).orElseThrow(() -> new NotFoundException(guestId));
            booking.addGuest(guest, i == 0);
        }

        return this.bookingRepository.save(booking);
    }

    public Booking checkIn(UUID bookingId) {
        Booking booking = this.findById(bookingId);
        if (booking.getStatus() != BookingStatus.CONFIRMED) {
            throw new ConflictException("La prenotazione non e' in uno stato valido per il check-in (stato attuale: " + booking.getStatus() + ")");
        }
        booking.setStatus(BookingStatus.CHECKED_IN);
        booking.setCheckedInAt(LocalDateTime.now());
        Booking saved = this.bookingRepository.save(booking);

        this.digitalKeyService.issueKeysForBooking(saved);

        return saved;
    }

    public Booking checkOut(UUID bookingId) {
        Booking booking = this.findById(bookingId);
        if (booking.getStatus() != BookingStatus.CHECKED_IN) {
            throw new ConflictException("La prenotazione non e' in uno stato valido per il check-out (stato attuale: " + booking.getStatus() + ")");
        }
        booking.setStatus(BookingStatus.CHECKED_OUT);
        booking.setCheckedOutAt(LocalDateTime.now());
        Booking saved = this.bookingRepository.save(booking);

        this.digitalKeyService.revokeKeysForBooking(saved);

        return saved;
    }

    public Booking cancel(UUID bookingId) {
        Booking booking = this.findById(bookingId);
        if (booking.getStatus() != BookingStatus.CONFIRMED) {
            throw new ConflictException("Solo una prenotazione confermata (non ancora iniziata) puo' essere annullata");
        }
        booking.setStatus(BookingStatus.CANCELLED);
        return this.bookingRepository.save(booking);
    }

    public Booking addExtraService(UUID bookingId, AddExtraServiceDTO body) {
        Booking booking = this.findById(bookingId);
        ExtraService extraService = this.extraServiceRepository.findById(body.extraServiceId())
                .orElseThrow(() -> new NotFoundException(body.extraServiceId()));

        BookingExtraService line = new BookingExtraService(booking, extraService, body.quantity(), extraService.getPrice());
        this.bookingExtraServiceRepository.save(line);

        booking.setTotalPrice(booking.getTotalPrice().add(line.getLineTotal()));
        return this.bookingRepository.save(booking);
    }
}
