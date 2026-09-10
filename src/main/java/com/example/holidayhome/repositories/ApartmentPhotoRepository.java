package com.example.holidayhome.repositories;

import com.example.holidayhome.entities.ApartmentPhoto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ApartmentPhotoRepository extends JpaRepository<ApartmentPhoto, UUID> {

    List<ApartmentPhoto> findByApartmentId(UUID apartmentId);
}
