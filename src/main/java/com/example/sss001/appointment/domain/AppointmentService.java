package com.example.sss001.appointment.domain;

import com.example.sss001.appointment.infrastructure.AppointmentRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AppointmentService {

    private final AppointmentRepository repository;

    public AppointmentService(AppointmentRepository repository) {
        this.repository = repository;
    }

    public List<Appointment> findAll() {
        return repository.findAll();
    }

    public Appointment findById(Long id) {
        return repository.findById(id).orElse(null);
    }

    public List<Appointment> findByPatientId(Long patientId) {
        return repository.findByPatientId(patientId);
    }

    public List<Appointment> findByProfessionalId(Long professionalId) {
        return repository.findByProfessionalId(professionalId);
    }

    public List<Appointment> findByPatientEmail(String email) {
        return repository.findByPatientUserEmail(email);
    }

    public List<Appointment> findByProfessionalEmail(String email) {
        return repository.findByProfessionalUserEmail(email);
    }

    public Appointment save(Appointment appointment) {
        return repository.save(appointment);
    }

    public void delete(Long id) {
        repository.deleteById(id);
    }
}