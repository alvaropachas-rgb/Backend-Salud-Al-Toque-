package com.example.sss001.appointment.dto;

import lombok.*;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AppointmentDTO {

    private Long id;

    private LocalDate date;

    private LocalTime time;

    private String status;

    private String notes;

    private Double price;

    private Long patientId;

    private String patientName;

    private Long professionalId;

    private String professionalName;
}