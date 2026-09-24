package com.example.sss001.appointment.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreateAppointmentRequest {

    private String date;
    private String time;
    private String notes;
    private Long professionalId;
}