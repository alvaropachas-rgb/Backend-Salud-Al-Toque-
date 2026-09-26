package com.example.sss001.medicalservice.domain;

import com.example.sss001.professional.domain.Professional;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "medical_services")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MedicalService {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String description;

    private Double price;

    @ManyToOne
    @JoinColumn(name = "professional_id")
    private Professional professional;
}