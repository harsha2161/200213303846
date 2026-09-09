package com.example.identify_prevent_duplicates.Repo;

import com.example.identify_prevent_duplicates.model.Nomination;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface NominationRepo extends JpaRepository<Nomination, String> {

    boolean existsByEmployee_nameAndTraining_program_id(String employeeName, String trainingProgramId);

    // Count how many CONFIRMED nominations exist for a training program
    long countByTraining_program_idAndStatus(String trainingProgramId, String status);

    // Find the first person on the waiting list (ordered by nominated_at ASC) for promotion
    Optional<Nomination> findFirstByTraining_program_idAndStatusOrderByNominated_atAsc(String trainingProgramId, String status);

    // Get all nominations for a specific training program
    List<Nomination> findByTraining_program_idOrderByNominated_atAsc(String trainingProgramId);

    // Get all nominations
    List<Nomination> findAllByOrderByNominated_atDesc();
}
