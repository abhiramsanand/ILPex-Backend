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
            "t.percipioEmail, " +
            "r.score, " +
            "CASE WHEN r.score IS NOT NULL THEN 'Completed' ELSE 'Not Completed' END, " +
            "a.assessmentName) " +
            "FROM Results r " +
            "JOIN r.trainees t " +
            "JOIN r.assessmentBatchAllocation aba " +
            "JOIN aba.assessments a")
    List<TraineeAssessmentDTO> fetchTraineeAssessmentDetails();
}
