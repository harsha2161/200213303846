package com.example.identify_prevent_duplicates.model;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "training_programs")
@Data
@NoArgsConstructor
@AllArgsConstructor

public class TrainingProgram {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(name = "training_date", nullable = false)
    private LocalDate trainingDate;

    private String venue;

    @Column(name = "resource_person")
    private String resourcePerson;

    @Column(name = "max_participants")
    private Integer maxParticipants;
}
