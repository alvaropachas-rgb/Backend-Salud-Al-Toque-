package com.example.sss001.professional.domain;

import com.example.sss001.professional.infrastructure.ProfessionalRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProfessionalService {

    private final ProfessionalRepository repository;

    public ProfessionalService(ProfessionalRepository repository) {
        this.repository = repository;
    }

    public List<Professional> findAll() {
        return repository.findAll();
    }

    public List<Professional> findBySpecialty(String specialty) {
        return repository.findBySpecialtyName(specialty);
    }

    public Professional findById(Long id) {
        return repository.findById(id).orElse(null);
    }

    public Professional save(Professional professional) {
        return repository.save(professional);
    }

    public void delete(Long id) {
        repository.deleteById(id);
    }
}