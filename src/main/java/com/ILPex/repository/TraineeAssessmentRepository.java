package com.ILPex.repository;

import com.ILPex.DTO.TraineeAssessmentDTO;
import com.ILPex.entity.Results;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TraineeAssessmentRepository extends JpaRepository<Results, Long> {

    @Query("SELECT new com.ILPex.DTO.TraineeAssessmentDTO(" +
            "u.userName, " +  // Fetch the username from the Users entity
            "r.score, " +
            "CASE WHEN r.score IS NOT NULL THEN 'Completed' ELSE 'Not Completed' END, " +
            "a.assessmentName) " +
            "FROM Results r " +
            "JOIN r.trainees t " +  // Join with the Trainees entity
            "JOIN t.users u " +  // Join with the Users entity to get the username
            "JOIN r.assessmentBatchAllocation aba " +  // Join with the AssessmentBatchAllocation entity
            "JOIN aba.assessments a")
    List<TraineeAssessmentDTO> fetchTraineeAssessmentDetails();
}
