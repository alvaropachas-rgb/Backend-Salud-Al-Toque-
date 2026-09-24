package com.example.sss001.professional.infrastructure;

import com.example.sss001.professional.domain.Professional;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProfessionalRepository
        extends JpaRepository<Professional, Long> {

    List<Professional> findBySpecialtyName(String name);
}