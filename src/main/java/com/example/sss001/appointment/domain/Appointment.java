package com.example.sss001.appointment.domain;

import com.example.sss001.patient.domain.Patient;
import com.example.sss001.professional.domain.Professional;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalTime;

// La restricción única evita que dos citas queden guardadas para
// el mismo profesional, en la misma fecha y a la misma hora.
// Es la última línea de defensa contra la doble reserva: aunque
// dos peticiones lleguen al mismo tiempo (condición de carrera),
// la base de datos solo va a permitir que una se guarde.
@Entity
@Table(
        name = "appointments",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_professional_date_time",
                columnNames = {"professional_id", "date", "time"}
        )
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Appointment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDate date;

    private LocalTime time;

    private String status;

    private String notes;

    private Double price;

    @ManyToOne
    @JoinColumn(name = "patient_id")
    private Patient patient;

    @ManyToOne
    @JoinColumn(name = "professional_id")
    private Professional professional;
}