package com.example.identify_prevent_duplicates.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "Departments")
@Data
@NoArgsConstructor
@AllArgsConstructor

public class Departments {
    @Id
    private String department_id;

    @Column(nullable = false, unique = true)
    private String name;



}
