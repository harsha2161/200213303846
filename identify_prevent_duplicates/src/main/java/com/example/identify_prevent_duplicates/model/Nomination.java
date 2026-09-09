package com.example.identify_prevent_duplicates.model;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "nominations", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"employee_name", "training_program_id"})
})
@Data
@NoArgsConstructor
@AllArgsConstructor

public class Nomination {
    @Id
    private String nominate_id;
    private String employee_name;
    private String department_id;
    private String training_program_id;
    private String status;
    private LocalDateTime nominated_at;
}
