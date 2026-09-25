package com.example.sss001.appointment.infrastructure;

import com.example.sss001.appointment.domain.Appointment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public interface AppointmentRepository
        extends JpaRepository<Appointment, Long> {

    List<Appointment> findByPatientId(Long patientId);

    List<Appointment> findByProfessionalId(Long professionalId);

    List<Appointment> findByPatientUserEmail(String email);

    List<Appointment> findByProfessionalUserEmail(String email);

    // Usado para validar disponibilidad antes de guardar una cita
    // y evitar que dos pacientes reserven el mismo horario.
    boolean existsByProfessionalIdAndDateAndTimeAndStatusNot(
            Long professionalId,
            LocalDate date,
            LocalTime time,
            String status
    );
}