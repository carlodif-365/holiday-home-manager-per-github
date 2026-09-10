package com.example.holidayhome.runner;

import com.example.holidayhome.entities.Apartment;
import com.example.holidayhome.entities.ApartmentDoor;
import com.example.holidayhome.entities.MainEntranceDoor;
import com.example.holidayhome.repositories.ApartmentDoorRepository;
import com.example.holidayhome.repositories.ApartmentRepository;
import com.example.holidayhome.repositories.MainEntranceDoorRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

/**
 * Popola la struttura al primo avvio: la palazzina (portone + 16 appartamenti,
 * ciascuno con la propria porta) se il database e' vuoto. Idempotente: non
 * duplica nulla se eseguito piu' volte.
 */
@Component
public class DataSeeder implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataSeeder.class);

    private final ApartmentRepository apartmentRepository;
    private final ApartmentDoorRepository apartmentDoorRepository;
    private final MainEntranceDoorRepository mainEntranceDoorRepository;
    private final String mainEntranceLabel;
    private final int totalApartments;

    public DataSeeder(ApartmentRepository apartmentRepository,
                       ApartmentDoorRepository apartmentDoorRepository,
                       MainEntranceDoorRepository mainEntranceDoorRepository,
                       @Value("${property.mainEntranceLabel}") String mainEntranceLabel,
                       @Value("${property.totalApartments}") int totalApartments) {
        this.apartmentRepository = apartmentRepository;
        this.apartmentDoorRepository = apartmentDoorRepository;
        this.mainEntranceDoorRepository = mainEntranceDoorRepository;
        this.mainEntranceLabel = mainEntranceLabel;
        this.totalApartments = totalApartments;
    }

    @Override
    public void run(String... args) {
        if (this.mainEntranceDoorRepository.count() == 0) {
            MainEntranceDoor mainDoor = new MainEntranceDoor("LOCK-MAIN-ENTRANCE", mainEntranceLabel);
            this.mainEntranceDoorRepository.save(mainDoor);
            log.info("Creato il portone principale della struttura ({})", mainEntranceLabel);
        }

        if (this.apartmentRepository.count() > 0) {
            return; // struttura gia' popolata
        }

        int apartmentsPerFloor = 4;
        int created = 0;
        int floor = 1;

        while (created < totalApartments) {
            for (int unit = 1; unit <= apartmentsPerFloor && created < totalApartments; unit++) {
                String code = String.format("%d%02d", floor, unit);
                int maxGuests = 2 + (unit % 3) * 2; // 2, 4 o 6 posti letto
                double size = 35 + (unit * 5);
                BigDecimal price = BigDecimal.valueOf(60 + (floor * 5) + (unit * 3));

                Apartment apartment = new Apartment(code, floor, maxGuests, size,
                        "Appartamento al piano " + floor + ", fino a " + maxGuests + " ospiti", price);
                Apartment saved = this.apartmentRepository.save(apartment);

                ApartmentDoor door = new ApartmentDoor("LOCK-" + saved.getCode(), saved);
                this.apartmentDoorRepository.save(door);

                created++;
            }
            floor++;
        }

        log.info("Creati {} appartamenti (con relativa porta digitale)", created);
    }
}
