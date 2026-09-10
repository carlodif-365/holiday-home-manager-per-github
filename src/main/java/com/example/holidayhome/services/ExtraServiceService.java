package com.example.holidayhome.services;

import com.example.holidayhome.entities.ExtraService;
import com.example.holidayhome.exceptions.NotFoundException;
import com.example.holidayhome.exceptions.ValidationException;
import com.example.holidayhome.payloads.extraservices.NewExtraServiceDTO;
import com.example.holidayhome.repositories.ExtraServiceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class ExtraServiceService {

    @Autowired
    private ExtraServiceRepository extraServiceRepository;

    public List<ExtraService> findAll() {
        return this.extraServiceRepository.findAll();
    }

    public ExtraService findById(UUID id) {
        return this.extraServiceRepository.findById(id).orElseThrow(() -> new NotFoundException(id));
    }

    public ExtraService create(NewExtraServiceDTO body) {
        if (this.extraServiceRepository.findByName(body.name()).isPresent()) {
            throw new ValidationException("Esiste gia' un servizio con questo nome");
        }
        ExtraService extraService = new ExtraService(body.name(), body.description(), body.price());
        return this.extraServiceRepository.save(extraService);
    }

    public void delete(UUID id) {
        ExtraService found = this.findById(id);
        this.extraServiceRepository.delete(found);
    }
}
