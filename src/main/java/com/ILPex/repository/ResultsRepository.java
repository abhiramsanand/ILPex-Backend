package com.ILPex.repository;

import com.ILPex.DTO.TraineeAssessmentDTO;
import com.ILPex.entity.Results;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ResultsRepository extends JpaRepository<Results,Long> {
    Optional<Results> findByAssessmentBatchesAllocationIdAndTraineeId(int assessmentBatchAllocationId, int traineeId);
//    @Query("SELECT new com.ILPex.dto.TraineeAssessmentDTO(a.id, a.assessmentName, r.score, aba.endDate, r.updatedAt) " +
//            "FROM Results r " +
//            "JOIN r.assessmentBatchAllocation aba " +
//            "JOIN aba.assessments a " +
//            "WHERE r.traineeId = :traineeId")
//    List<TraineeAssessmentDTO> findAssessmentsByTraineeId(@Param("traineeId") int traineeId);
}
