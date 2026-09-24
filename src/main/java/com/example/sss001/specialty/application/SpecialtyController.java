package com.example.sss001.specialty.application;

import com.example.sss001.specialty.domain.Specialty;
import com.example.sss001.specialty.domain.SpecialtyService;
import com.example.sss001.specialty.dto.SpecialtyDTO;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/specialties")
public class SpecialtyController {

    private final SpecialtyService service;

    public SpecialtyController(SpecialtyService service) {
        this.service = service;
    }

    @GetMapping
    public List<SpecialtyDTO> findAll() {

        return service.findAll()
                .stream()
                .map(this::convertToDTO)
                .toList();
    }

    @GetMapping("/{id}")
    public SpecialtyDTO findById(
            @PathVariable Long id) {

        Specialty specialty = service.findById(id);

        if (specialty == null) {
            return null;
        }

        return convertToDTO(specialty);
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public SpecialtyDTO save(
            @RequestBody Specialty specialty) {

        return convertToDTO(
                service.save(specialty)
        );
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public void delete(
            @PathVariable Long id) {

        service.delete(id);
    }

    private SpecialtyDTO convertToDTO(
            Specialty specialty) {

        return new SpecialtyDTO(
                specialty.getId(),
                specialty.getName()
        );
    }
}