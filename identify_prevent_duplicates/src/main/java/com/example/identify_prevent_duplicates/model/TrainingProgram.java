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
    private String training_program_id;
    private String name;
    private int max_capacity;
}
