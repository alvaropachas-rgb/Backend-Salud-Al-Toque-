package com.example.sss001.appointment.dto;

import lombok.*;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreateAppointmentRequest {

    // Formato esperado en el JSON: "2026-10-10" y "11:00"
    private LocalDate date;
    private LocalTime time;
    private String notes;
    private Long professionalId;
}