package com.example.sss001.appointment.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AppointmentDTO {

    private Long id;

    private String date;

    private String time;

    private String status;

    private String notes;

    private Double price;

    private Long patientId;

    private String patientName;

    private Long professionalId;

    private String professionalName;
}