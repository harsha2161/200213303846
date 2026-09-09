package com.example.identify_prevent_duplicates.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor

public class NominationDTO {
    private String nominate_id;
    private String employee_name;
    private String department_id;
    private String training_program_id;
    private String status;
    private String nominated_at;
}
