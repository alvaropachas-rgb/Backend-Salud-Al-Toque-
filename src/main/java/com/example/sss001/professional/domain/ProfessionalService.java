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

    public List<Professional> findBySpecialty(
            String specialty) {

        return repository.findBySpecialtyNameIgnoreCase(
                specialty
        );
    }

    public List<Professional> search(
            String specialty,
            String location,
            Double maxPrice) {

        boolean hasSpecialty =
                specialty != null && !specialty.isBlank();

        boolean hasLocation =
                location != null && !location.isBlank();

        boolean hasMaxPrice =
                maxPrice != null;

        // 1. Especialidad + ubicación + precio
        if (hasSpecialty &&
                hasLocation &&
                hasMaxPrice) {
            return repository
                    .findDistinctBySpecialtyNameIgnoreCaseAndLocationIgnoreCaseAndMedicalServicesPriceLessThanEqual(
                            specialty,
                            location,
                            maxPrice
                    );
        }
        // 2. Especialidad + ubicación
        if (hasSpecialty && hasLocation) {
            return repository
                    .findBySpecialtyNameIgnoreCaseAndLocationIgnoreCase(
                            specialty,
                            location
                    );
        }
        // 3. Especialidad + precio
        if (hasSpecialty && hasMaxPrice) {
            return repository.findDistinctBySpecialtyNameIgnoreCaseAndMedicalServicesPriceLessThanEqual(specialty, maxPrice);
        }
        // 4. Ubicación + precio
        if (hasLocation && hasMaxPrice) {
            return repository.findDistinctByLocationIgnoreCaseAndMedicalServicesPriceLessThanEqual(location, maxPrice);
        }
        // 5. Solo especialidad
        if (hasSpecialty) {
            return repository.findBySpecialtyNameIgnoreCase(specialty);
        }
        // 6. Solo ubicación
        if (hasLocation) {
            return repository.findByLocationIgnoreCase(location);
        }
        // 7. Solo precio
        if (hasMaxPrice) {
            return repository.findDistinctByMedicalServicesPriceLessThanEqual(maxPrice);
        }
        // 8. Sin filtros
        return repository.findAll();
    }

    public Professional findById(Long id) {
        return repository.findById(id).orElse(null);
    }

    public Professional findByUserEmail(String email) {
        return repository.findByUserEmail(email)
                .orElse(null);
    }

    public Professional save(Professional professional) {
        return repository.save(professional);
    }

    public void delete(Long id) {
        repository.deleteById(id);
    }
}