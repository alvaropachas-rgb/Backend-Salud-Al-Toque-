package com.example.sss001.professional.domain;

import com.example.sss001.specialty.domain.Specialty;
import com.example.sss001.user.domain.User;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "professionals")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Professional {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String location;

    private Double price;

    private Double rating;

    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "specialty_id")
    private Specialty specialty;
}