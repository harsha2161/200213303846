package com.example.identify_prevent_duplicates.model;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "nominations", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"employee_id", "training_program_id"})
})
@Data
@NoArgsConstructor
@AllArgsConstructor

public class Nomination {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "employee_number")
    private Long employee_number;

    @Column(name = "nominated_at")
    private LocalDateTime nominatedAt = LocalDateTime.now();
}
