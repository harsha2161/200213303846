package com.example.identify_prevent_duplicates.service.Impls;

import com.example.identify_prevent_duplicates.DTO.NominationDTO;
import com.example.identify_prevent_duplicates.DTO.ResponseDTO;
import com.example.identify_prevent_duplicates.Repo.EmployeeRepo;
import com.example.identify_prevent_duplicates.Repo.NominationRepo;
import com.example.identify_prevent_duplicates.Repo.TrainingProgramRepo;
import com.example.identify_prevent_duplicates.eligibility.EligibilityContext;
import com.example.identify_prevent_duplicates.eligibility.EligibilityEngine;
import com.example.identify_prevent_duplicates.exception.DuplicateNominationException;
import com.example.identify_prevent_duplicates.model.Employee;
import com.example.identify_prevent_duplicates.model.Nomination;
import com.example.identify_prevent_duplicates.model.TrainingProgram;
import com.example.identify_prevent_duplicates.service.NominationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class NominationServiceImpl implements NominationService {

    @Autowired
    private NominationRepo nominationRepo;

    @Autowired
    private TrainingProgramRepo trainingProgramRepo;

    @Autowired
    private EmployeeRepo employeeRepo;

    @Autowired
    private EligibilityEngine eligibilityEngine;

    @Override
    @Transactional
    public ResponseEntity<ResponseDTO> addNomination(NominationDTO nominationDTO) {

        // 1. Look up the training program
        TrainingProgram program = trainingProgramRepo
                .findById(nominationDTO.getTraining_program_id())
                .orElseThrow(() -> new RuntimeException("Training program not found: " + nominationDTO.getTraining_program_id()));

        // 2. Look up optional Employee record if exists
        Employee employee = null;
        if (nominationDTO.getEmployee_name() != null) {
            employee = employeeRepo.findByNameIgnoreCase(nominationDTO.getEmployee_name()).orElse(null);
        }

        // 3. Evaluate extensible eligibility rules (Department, Management grade/service, 12-Month Cooldown, etc.)
        EligibilityContext context = EligibilityContext.builder()
                .nominationDTO(nominationDTO)
                .trainingProgram(program)
                .employee(employee)
                .build();
        eligibilityEngine.validate(context);

        // 4. Check for duplicate nomination
        boolean isDuplicate = nominationRepo.existsByEmployeeNameAndProgramId(
                nominationDTO.getEmployee_name(),
                nominationDTO.getTraining_program_id()
        );

        if (isDuplicate) {
            throw new DuplicateNominationException("Employee already nominated for this training program");
        }

        // 5. Count current confirmed nominations
        long confirmedCount = nominationRepo.countByProgramIdAndStatus(
                nominationDTO.getTraining_program_id(), "CONFIRMED"
        );

        // 6. Determine status based on capacity
        String status;
        if (confirmedCount < program.getMax_capacity()) {
            status = "CONFIRMED";
        } else {
            status = "WAITING";
        }

        // 7. Build and save the Nomination entity
        Nomination nomination = Nomination.builder()
                .nominate_id(nominationDTO.getNominate_id())
                .employee_name(nominationDTO.getEmployee_name())
                .training_program_id(nominationDTO.getTraining_program_id())
                .department_id(nominationDTO.getDepartment_id())
                .designation(nominationDTO.getDesignation() != null ? nominationDTO.getDesignation() : (employee != null ? employee.getDesignation() : null))
                .years_of_service(nominationDTO.getYears_of_service() != null ? nominationDTO.getYears_of_service() : (employee != null ? employee.getYears_of_service() : null))
                .status(status)
                .nominated_at(LocalDateTime.now())
                .build();

        nominationRepo.save(nomination);

        // 8. Build response
        String message;
        if ("CONFIRMED".equals(status)) {
            message = "Nomination confirmed successfully! Seat " + (confirmedCount + 1) + " of " + program.getMax_capacity();
        } else {
            message = "Training program is full. You have been placed on the waiting list.";
        }

        ResponseDTO responseDTO = ResponseDTO.builder()
                .message(message)
                .status(HttpStatus.CREATED)
                .data(status)
                .build();

        return new ResponseEntity<>(responseDTO, HttpStatus.CREATED);
    }

    @Override
    @Transactional
    public ResponseEntity<ResponseDTO> cancelNomination(String nominateId) {

        // 1. Find the nomination
        Nomination nomination = nominationRepo.findById(nominateId)
                .orElseThrow(() -> new RuntimeException("Nomination not found: " + nominateId));

        String previousStatus = nomination.getStatus();
        String trainingProgramId = nomination.getTraining_program_id();

        // 2. Mark as CANCELLED
        nomination.setStatus("CANCELLED");
        nominationRepo.save(nomination);

        // 3. If a CONFIRMED person cancelled, promote the first WAITING person
        String promotedMessage = "";
        if ("CONFIRMED".equals(previousStatus)) {
            Optional<Nomination> firstWaiting = nominationRepo
                    .findFirstWaiting(trainingProgramId, "WAITING");

            if (firstWaiting.isPresent()) {
                Nomination promoted = firstWaiting.get();
                promoted.setStatus("CONFIRMED");
                nominationRepo.save(promoted);
                promotedMessage = " Employee '" + promoted.getEmployee_name() + "' has been promoted from the waiting list.";
            }
        }

        ResponseDTO responseDTO = ResponseDTO.builder()
                .message("Nomination cancelled successfully." + promotedMessage)
                .status(HttpStatus.OK)
                .build();

        return new ResponseEntity<>(responseDTO, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<ResponseDTO> getAllNominations() {
        List<Nomination> nominations = nominationRepo.findAllOrderByNominatedAtDesc();

        ResponseDTO responseDTO = ResponseDTO.builder()
                .message("Nominations retrieved successfully")
                .status(HttpStatus.OK)
                .data(nominations)
                .build();

        return new ResponseEntity<>(responseDTO, HttpStatus.OK);
    }
}