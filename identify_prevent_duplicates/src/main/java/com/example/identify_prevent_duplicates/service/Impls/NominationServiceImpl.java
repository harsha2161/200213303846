package com.example.identify_prevent_duplicates.service.Impls;

import com.example.identify_prevent_duplicates.DTO.NominationDTO;
import com.example.identify_prevent_duplicates.DTO.ResponseDTO;
import com.example.identify_prevent_duplicates.exception.DuplicateNominationException;
import com.example.identify_prevent_duplicates.Repo.NominationRepo;
import com.example.identify_prevent_duplicates.Repo.TrainingProgramRepo;
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

    @Override
    @Transactional
    public ResponseEntity<ResponseDTO> addNomination(NominationDTO nominationDTO) {

        // 1. Check for duplicate nomination
        boolean isDuplicate = nominationRepo.existsByEmployee_nameAndTraining_program_id(
                nominationDTO.getEmployee_name(),
                nominationDTO.getTraining_program_id()
        );

        if (isDuplicate) {
            throw new DuplicateNominationException("Employee already nominated for this training program");
        }

        // 2. Look up the training program to get max_capacity
        TrainingProgram program = trainingProgramRepo
                .findById(nominationDTO.getTraining_program_id())
                .orElseThrow(() -> new RuntimeException("Training program not found: " + nominationDTO.getTraining_program_id()));

        // 3. Count current confirmed nominations
        long confirmedCount = nominationRepo.countByTraining_program_idAndStatus(
                nominationDTO.getTraining_program_id(), "CONFIRMED"
        );

        // 4. Determine status based on capacity
        String status;
        if (confirmedCount < program.getMax_capacity()) {
            status = "CONFIRMED";
        } else {
            status = "WAITING";
        }

        // 5. Build and save the Nomination entity
        Nomination nomination = new Nomination();
        nomination.setNominate_id(nominationDTO.getNominate_id());
        nomination.setEmployee_name(nominationDTO.getEmployee_name());
        nomination.setTraining_program_id(nominationDTO.getTraining_program_id());
        nomination.setDepartment_id(nominationDTO.getDepartment_id());
        nomination.setStatus(status);
        nomination.setNominated_at(LocalDateTime.now());
        nominationRepo.save(nomination);

        // 6. Build response
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
                    .findFirstByTraining_program_idAndStatusOrderByNominated_atAsc(trainingProgramId, "WAITING");

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
        List<Nomination> nominations = nominationRepo.findAllByOrderByNominated_atDesc();

        ResponseDTO responseDTO = ResponseDTO.builder()
                .message("Nominations retrieved successfully")
                .status(HttpStatus.OK)
                .data(nominations)
                .build();

        return new ResponseEntity<>(responseDTO, HttpStatus.OK);
    }
}