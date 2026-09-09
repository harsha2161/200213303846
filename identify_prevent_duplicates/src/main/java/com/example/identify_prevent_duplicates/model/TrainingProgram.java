package com.example.identify_prevent_duplicates.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "training_programs")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TrainingProgram {
    @Id
    private String training_program_id;
    private String name;
    private int max_capacity;
    private String category;
    private String allowed_departments;
    private String required_designation;
    private Integer min_years_of_service;
}

