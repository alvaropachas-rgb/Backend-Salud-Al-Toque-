package com.example.sss001.review.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ReviewDTO {

    private Long id;
    private Integer rating;
    private String comment;
    private Long appointmentId;
    private Long patientId;
    private Long professionalId;
}