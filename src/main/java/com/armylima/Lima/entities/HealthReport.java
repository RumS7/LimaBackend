package com.armylima.Lima.entities;


import com.armylima.Lima.dto.HealthStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HealthReport {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String armyId;

    private LocalDate reportDate;

    @Enumerated(EnumType.STRING)
    private HealthStatus status;

    @Column(nullable = true) // Symptoms can be optional
    private String symptoms;
}
