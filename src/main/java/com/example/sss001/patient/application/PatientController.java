package com.example.sss001.patient.application;

import com.example.sss001.patient.domain.Patient;
import com.example.sss001.patient.domain.PatientService;
import com.example.sss001.patient.dto.PatientDTO;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/patients")
public class PatientController {

    private final PatientService service;

    public PatientController(PatientService service) {
        this.service = service;
    }

    // Un paciente autenticado puede ver su propio perfil.
    @GetMapping("/me")
    @PreAuthorize("hasRole('PATIENT')")
    public PatientDTO me(Authentication authentication) {

        String email = authentication.getName();
        Patient patient = service.findByUserEmail(email);

        if (patient == null) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "No se encontró tu perfil de paciente"
            );
        }

        return convertToDTO(patient);
    }

    // El resto es solo para administración interna.
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public List<PatientDTO> findAll() {

        return service.findAll()
                .stream()
                .map(this::convertToDTO)
                .toList();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public PatientDTO findById(@PathVariable Long id) {

        Patient patient = service.findById(id);

        if (patient == null) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Paciente no encontrado"
            );
        }

        return convertToDTO(patient);
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public Patient save(@RequestBody Patient patient) {
        return service.save(patient);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }

    private PatientDTO convertToDTO(Patient patient) {

        PatientDTO dto = new PatientDTO();

        dto.setId(patient.getId());
        dto.setAddress(patient.getAddress());
        dto.setDateOfBirth(patient.getDateOfBirth());

        if (patient.getUser() != null) {
            dto.setUserId(patient.getUser().getId());
            dto.setName(patient.getUser().getName());
            dto.setEmail(patient.getUser().getEmail());
        }

        return dto;
    }
}