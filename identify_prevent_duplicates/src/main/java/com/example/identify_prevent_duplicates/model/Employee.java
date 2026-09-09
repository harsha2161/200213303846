package com.example.identify_prevent_duplicates.model;

import jakarta.persistence.Entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "employees")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Employee {

    @Id
    private String employee_Id;

    @Column(nullable = false)
    private String name;



}
