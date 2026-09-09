package com.example.identify_prevent_duplicates.Repo;

import com.example.identify_prevent_duplicates.model.Nomination;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface NominationRepo extends JpaRepository<Nomination, String> {

    @Query("SELECT COUNT(n) > 0 FROM Nomination n WHERE LOWER(n.employee_name) = LOWER(:employeeName) AND n.training_program_id = :trainingProgramId")
    boolean existsByEmployeeNameAndProgramId(
            @Param("employeeName") String employeeName,
            @Param("trainingProgramId") String trainingProgramId
    );

    @Query("SELECT COUNT(n) FROM Nomination n WHERE n.training_program_id = :trainingProgramId AND n.status = :status")
    long countByProgramIdAndStatus(
            @Param("trainingProgramId") String trainingProgramId,
            @Param("status") String status
    );

    @Query("SELECT n FROM Nomination n WHERE n.training_program_id = :trainingProgramId AND n.status = :status ORDER BY n.nominated_at ASC LIMIT 1")
    Optional<Nomination> findFirstWaiting(
            @Param("trainingProgramId") String trainingProgramId,
            @Param("status") String status
    );

    @Query("SELECT n FROM Nomination n WHERE n.training_program_id = :trainingProgramId ORDER BY n.nominated_at ASC")
    List<Nomination> findByProgramId(
            @Param("trainingProgramId") String trainingProgramId
    );

    @Query("SELECT n FROM Nomination n ORDER BY n.nominated_at DESC")
    List<Nomination> findAllOrderByNominatedAtDesc();

    @Query("SELECT n FROM Nomination n WHERE LOWER(n.employee_name) = LOWER(:employeeName) AND n.training_program_id = :programId AND n.status <> 'CANCELLED' AND n.nominated_at >= :sinceDate")
    List<Nomination> findRecentNominations(
            @Param("employeeName") String employeeName,
            @Param("programId") String programId,
            @Param("sinceDate") LocalDateTime sinceDate
    );
}


