package com.example.sss001.professional.application;

import com.example.sss001.professional.domain.Professional;
import com.example.sss001.professional.domain.ProfessionalService;
import com.example.sss001.professional.dto.ProfessionalDTO;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/professionals")
public class ProfessionalController {

    private final ProfessionalService service;

    public ProfessionalController(ProfessionalService service) {
        this.service = service;
    }

    /*
     * GET /professionals
     * GET /professionals?specialty=Cardiologia
     * GET /professionals?specialty=Dermatologia
     * */

    @GetMapping
    public List<ProfessionalDTO> findAll(
            @RequestParam(required = false) String specialty) {

        List<Professional> professionals;

        if (specialty == null || specialty.isBlank()) {
            professionals = service.findAll();
        } else {
            professionals = service.findBySpecialty(specialty);
        }

        return professionals
                .stream()
                .map(this::convertToDTO)
                .toList();
    }

    @GetMapping("/{id}")
    public ProfessionalDTO findById(@PathVariable Long id) {

        Professional professional = service.findById(id);

        if (professional == null) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Profesional no encontrado"
            );
        }

        return convertToDTO(professional);
    }

    // TODO: cuando exista el flujo de "crear perfil profesional"
    // (Persona 1 / MVP), este endpoint deberá permitir también
    // que el propio profesional actualice su perfil, no solo el ADMIN.
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public Professional save(@RequestBody Professional professional) {
        return service.save(professional);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }

    private ProfessionalDTO convertToDTO(Professional professional) {

        ProfessionalDTO dto = new ProfessionalDTO();

        dto.setId(professional.getId());
        if (professional.getSpecialty() != null) {
            dto.setSpecialty(professional.getSpecialty().getName());
        }
        dto.setLocation(professional.getLocation());
        dto.setPrice(professional.getPrice());
        dto.setRating(professional.getRating());

        if (professional.getUser() != null) {
            dto.setUserId(professional.getUser().getId());
            dto.setName(professional.getUser().getName());
        }

        return dto;
    }
}