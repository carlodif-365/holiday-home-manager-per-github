package com.example.holidayhome.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "apartments")
public class Apartment {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(nullable = false, unique = true)
    private String code; // es. "A101"

    @Column(nullable = false)
    private Integer floor;

    @Column(nullable = false)
    private Integer maxGuests;

    private Double sizeSqm;

    @Column(length = 2000)
    private String description;

    @Column(nullable = false)
    private BigDecimal pricePerNight;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ApartmentStatus status;

    @OneToMany(mappedBy = "apartment", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<ApartmentPhoto> photos = new ArrayList<>();

    @OneToOne(mappedBy = "apartment", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private ApartmentDoor apartmentDoor;

    public Apartment() {
    }

    public Apartment(String code, Integer floor, Integer maxGuests, Double sizeSqm, String description, BigDecimal pricePerNight) {
        this.code = code;
        this.floor = floor;
        this.maxGuests = maxGuests;
        this.sizeSqm = sizeSqm;
        this.description = description;
        this.pricePerNight = pricePerNight;
        this.status = ApartmentStatus.AVAILABLE;
    }

    public UUID getId() {
        return id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Integer getFloor() {
        return floor;
    }

    public void setFloor(Integer floor) {
        this.floor = floor;
    }

    public Integer getMaxGuests() {
        return maxGuests;
    }

    public void setMaxGuests(Integer maxGuests) {
        this.maxGuests = maxGuests;
    }

    public Double getSizeSqm() {
        return sizeSqm;
    }

    public void setSizeSqm(Double sizeSqm) {
        this.sizeSqm = sizeSqm;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getPricePerNight() {
        return pricePerNight;
    }

    public void setPricePerNight(BigDecimal pricePerNight) {
        this.pricePerNight = pricePerNight;
    }

    public ApartmentStatus getStatus() {
        return status;
    }

    public void setStatus(ApartmentStatus status) {
        this.status = status;
    }

    public List<ApartmentPhoto> getPhotos() {
        return photos;
    }

    public void addPhoto(ApartmentPhoto photo) {
        this.photos.add(photo);
        photo.setApartment(this);
    }

    public ApartmentDoor getApartmentDoor() {
        return apartmentDoor;
    }

    public void setApartmentDoor(ApartmentDoor apartmentDoor) {
        this.apartmentDoor = apartmentDoor;
    }

    @Override
    public String toString() {
        return "Apartment{" +
                "id=" + id +
                ", code='" + code + '\'' +
                ", floor=" + floor +
                ", status=" + status +
                '}';
    }
}
