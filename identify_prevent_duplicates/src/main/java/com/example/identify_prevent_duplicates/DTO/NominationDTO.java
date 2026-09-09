package com.example.identify_prevent_duplicates.DTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class NominationDTO {
    private String nominate_id;
    private String employee_name;
    private String department_id;
    private String training_program_id;
    private String designation;
    private Integer years_of_service;
    private String status;
    private String nominated_at;
}

