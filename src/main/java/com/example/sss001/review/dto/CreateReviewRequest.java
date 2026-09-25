package com.example.sss001.review.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreateReviewRequest {

    private Long appointmentId;
    private Integer rating;
    private String comment;
}