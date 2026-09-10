package com.example.holidayhome.services;

import com.example.holidayhome.entities.Guest;
import com.example.holidayhome.exceptions.NotFoundException;
import com.example.holidayhome.exceptions.ValidationException;
import com.example.holidayhome.payloads.guests.NewGuestDTO;
import com.example.holidayhome.repositories.GuestRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class GuestService {

    @Autowired
    private GuestRepository guestRepository;

    public List<Guest> findAll() {
        return this.guestRepository.findAll();
    }

    public Guest findById(UUID guestId) {
        return this.guestRepository.findById(guestId).orElseThrow(() -> new NotFoundException(guestId));
    }

    public List<Guest> searchByLastName(String lastName) {
        return this.guestRepository.findByLastNameContainingIgnoreCaseOrderByLastNameAsc(lastName);
    }

    public Guest create(NewGuestDTO body) {
        if (this.guestRepository.existsByDocumentNumber(body.documentNumber())) {
            throw new ValidationException("Esiste gia' un ospite con questo numero di documento");
        }
        Guest guest = new Guest(
                body.firstName(), body.lastName(), body.dateOfBirth(), body.placeOfBirth(), body.nationality(),
                body.documentType(), body.documentNumber(), body.documentExpiryDate(), body.email(), body.phone(), body.address()
        );
        return this.guestRepository.save(guest);
    }

    public Guest update(UUID guestId, NewGuestDTO body) {
        Guest found = this.findById(guestId);
        found.setFirstName(body.firstName());
        found.setLastName(body.lastName());
        found.setDateOfBirth(body.dateOfBirth());
        found.setPlaceOfBirth(body.placeOfBirth());
        found.setNationality(body.nationality());
        found.setDocumentType(body.documentType());
        found.setDocumentNumber(body.documentNumber());
        found.setDocumentExpiryDate(body.documentExpiryDate());
        found.setEmail(body.email());
        found.setPhone(body.phone());
        found.setAddress(body.address());
        return this.guestRepository.save(found);
    }

    public void delete(UUID guestId) {
        Guest found = this.findById(guestId);
        this.guestRepository.delete(found);
    }
}
