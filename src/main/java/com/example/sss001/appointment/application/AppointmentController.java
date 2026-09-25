package com.example.sss001.appointment.application;

import com.example.sss001.appointment.domain.Appointment;
import com.example.sss001.appointment.domain.AppointmentService;
import com.example.sss001.appointment.dto.AppointmentDTO;
import com.example.sss001.appointment.dto.CreateAppointmentRequest;
import com.example.sss001.patient.domain.Patient;
import com.example.sss001.patient.domain.PatientService;
import com.example.sss001.professional.domain.Professional;
import com.example.sss001.professional.domain.ProfessionalService;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/appointments")
public class AppointmentController {

    private final AppointmentService service;
    private final PatientService patientService;
    private final ProfessionalService professionalService;

    public AppointmentController(
            AppointmentService service,
            PatientService patientService,
            ProfessionalService professionalService) {

        this.service = service;
        this.patientService = patientService;
        this.professionalService = professionalService;
    }


    // ADMIN


    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public List<AppointmentDTO> findAll() {

        return service.findAll()
                .stream()
                .map(this::convertToDTO)
                .toList();
    }

    @GetMapping("/patient/{patientId}")
    @PreAuthorize("hasRole('ADMIN')")
    public List<AppointmentDTO> findByPatient(
            @PathVariable Long patientId) {

        return service.findByPatientId(patientId)
                .stream()
                .map(this::convertToDTO)
                .toList();
    }

    @GetMapping("/professional/{professionalId}")
    @PreAuthorize("hasRole('ADMIN')")
    public List<AppointmentDTO> findByProfessional(
            @PathVariable Long professionalId) {

        return service.findByProfessionalId(professionalId)
                .stream()
                .map(this::convertToDTO)
                .toList();
    }

    // PACIENTE

    @GetMapping("/my-appointments")
    @PreAuthorize("hasRole('PATIENT')")
    public List<AppointmentDTO> myAppointments(
            Authentication authentication) {

        String email = authentication.getName();

        return service.findByPatientEmail(email)
                .stream()
                .map(this::convertToDTO)
                .toList();
    }

    @PostMapping
    @PreAuthorize("hasRole('PATIENT')")
    public AppointmentDTO save(
            @RequestBody CreateAppointmentRequest request,
            Authentication authentication) {

        // Obtener el correo del usuario autenticado
        String email = authentication.getName();

        // Buscar automáticamente al paciente
        Patient patient = patientService.findByUserEmail(email);

        if (patient == null) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Paciente no encontrado"
            );
        }

        // Buscar al profesional seleccionado
        Professional professional =
                professionalService.findById(request.getProfessionalId());

        if (professional == null) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Profesional no encontrado"
            );
        }

        // Evitar doble reserva: ¿ya hay una cita activa para este
        // profesional en esa fecha y hora?
        boolean slotTaken = service.isSlotTaken(
                professional.getId(),
                request.getDate(),
                request.getTime()
        );

        if (slotTaken) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Ese horario ya no está disponible para este profesional"
            );
        }

        // Crear la cita
        Appointment appointment = new Appointment();

        appointment.setDate(request.getDate());
        appointment.setTime(request.getTime());
        appointment.setNotes(request.getNotes());

        // Datos controlados por el servidor
        appointment.setStatus("PENDIENTE");
        appointment.setPrice(professional.getPrice());

        // Relaciones
        appointment.setPatient(patient);
        appointment.setProfessional(professional);

        Appointment saved = service.save(appointment);

        return convertToDTO(saved);
    }

    // PROFESIONAL

    @GetMapping("/my-professional-appointments")
    @PreAuthorize("hasRole('PROFESSIONAL')")
    public List<AppointmentDTO> myProfessionalAppointments(
            Authentication authentication) {

        String email = authentication.getName();

        return service.findByProfessionalEmail(email)
                .stream()
                .map(this::convertToDTO)
                .toList();
    }

    // CONSULTA INDIVIDUAL

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public AppointmentDTO findById(
            @PathVariable Long id) {

        Appointment appointment = service.findById(id);

        if (appointment == null) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Cita no encontrada"
            );
        }

        return convertToDTO(appointment);
    }

    // DELETE

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public void delete(@PathVariable Long id) {

        service.delete(id);
    }

    // CONVERSIÓN ENTITY -> DTO

    private AppointmentDTO convertToDTO(Appointment appointment) {

        AppointmentDTO dto = new AppointmentDTO();

        dto.setId(appointment.getId());
        dto.setDate(appointment.getDate());
        dto.setTime(appointment.getTime());
        dto.setStatus(appointment.getStatus());
        dto.setNotes(appointment.getNotes());
        dto.setPrice(appointment.getPrice());

        if (appointment.getPatient() != null) {
            dto.setPatientId(appointment.getPatient().getId());

            if (appointment.getPatient().getUser() != null) {
                dto.setPatientName(
                        appointment.getPatient().getUser().getName()
                );
            }
        }

        if (appointment.getProfessional() != null) {
            dto.setProfessionalId(
                    appointment.getProfessional().getId()
            );

            if (appointment.getProfessional().getUser() != null) {
                dto.setProfessionalName(
                        appointment.getProfessional().getUser().getName()
                );
            }
        }
        return dto;
    }
}