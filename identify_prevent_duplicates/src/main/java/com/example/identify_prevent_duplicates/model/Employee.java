package com.example.identify_prevent_duplicates.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "employees")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Employee {

    @Id
    private String employee_Id;

    @Column(nullable = false)
    private String name;

    private String department_id;

    private String designation;

    private Integer years_of_service;

}
