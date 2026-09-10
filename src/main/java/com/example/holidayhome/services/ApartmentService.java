package com.example.holidayhome.services;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.example.holidayhome.entities.Apartment;
import com.example.holidayhome.entities.ApartmentDoor;
import com.example.holidayhome.entities.ApartmentPhoto;
import com.example.holidayhome.entities.ApartmentStatus;
import com.example.holidayhome.exceptions.NotFoundException;
import com.example.holidayhome.exceptions.ValidationException;
import com.example.holidayhome.payloads.apartments.NewApartmentDTO;
import com.example.holidayhome.repositories.ApartmentDoorRepository;
import com.example.holidayhome.repositories.ApartmentPhotoRepository;
import com.example.holidayhome.repositories.ApartmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class ApartmentService {

    @Autowired
    private ApartmentRepository apartmentRepository;
    @Autowired
    private ApartmentDoorRepository apartmentDoorRepository;
    @Autowired
    private ApartmentPhotoRepository apartmentPhotoRepository;
    @Autowired
    private Cloudinary cloudinaryUploader;

    public List<Apartment> findAll() {
        return this.apartmentRepository.findAll();
    }

    public Apartment findById(UUID apartmentId) {
        return this.apartmentRepository.findById(apartmentId).orElseThrow(() -> new NotFoundException(apartmentId));
    }

    public List<Apartment> findAvailable(LocalDate checkInDate, LocalDate checkOutDate, Integer minGuests) {
        if (!checkOutDate.isAfter(checkInDate)) {
            throw new ValidationException("La data di check-out deve essere successiva al check-in");
        }
        return this.apartmentRepository.findAvailableApartments(checkInDate, checkOutDate, minGuests == null ? 1 : minGuests);
    }

    public Apartment create(NewApartmentDTO body) {
        if (this.apartmentRepository.existsByCode(body.code())) {
            throw new ValidationException("Esiste gia' un appartamento con codice " + body.code());
        }

        Apartment apartment = new Apartment(body.code(), body.floor(), body.maxGuests(), body.sizeSqm(), body.description(), body.pricePerNight());
        Apartment saved = this.apartmentRepository.save(apartment);

        // Ogni appartamento ha la propria porta d'ingresso (gerarchia Door)
        ApartmentDoor door = new ApartmentDoor("LOCK-" + saved.getCode(), saved);
        this.apartmentDoorRepository.save(door);

        return saved;
    }

    public Apartment update(UUID apartmentId, NewApartmentDTO body) {
        Apartment found = this.findById(apartmentId);
        found.setFloor(body.floor());
        found.setMaxGuests(body.maxGuests());
        found.setSizeSqm(body.sizeSqm());
        found.setDescription(body.description());
        found.setPricePerNight(body.pricePerNight());
        return this.apartmentRepository.save(found);
    }

    public Apartment updateStatus(UUID apartmentId, ApartmentStatus status) {
        Apartment found = this.findById(apartmentId);
        found.setStatus(status);
        return this.apartmentRepository.save(found);
    }

    public Apartment uploadPhoto(UUID apartmentId, MultipartFile file, String caption) throws IOException {
        Apartment found = this.findById(apartmentId);
        Map<?, ?> response = this.cloudinaryUploader.uploader().upload(file.getBytes(), ObjectUtils.emptyMap());
        ApartmentPhoto photo = new ApartmentPhoto(response.get("secure_url").toString(), caption);
        found.addPhoto(photo);
        this.apartmentPhotoRepository.save(photo);
        return found;
    }

    public void delete(UUID apartmentId) {
        Apartment found = this.findById(apartmentId);
        this.apartmentRepository.delete(found);
    }
}
