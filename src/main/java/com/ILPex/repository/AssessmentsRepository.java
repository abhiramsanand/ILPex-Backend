package com.ILPex.repository;

import com.ILPex.DTO.AssessmentDetailsDTO;
import com.ILPex.DTO.TraineePendingAssessmentDTO;
import com.ILPex.entity.Assessments;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AssessmentsRepository extends JpaRepository<Assessments, Long> {

    @Query("SELECT new com.ILPex.DTO.AssessmentDetailsDTO(" + "a.assessmentName, " + "aba.batches.id, " +  // Fetch batchId
            "CASE WHEN aba.assessmentStatus = true THEN 'Completed' ELSE 'Active' END, " + "COUNT(r.traineeId)) " + "FROM Assessments a " + "JOIN a.assessmentBatchAllocations aba " + "LEFT JOIN aba.results r " + "GROUP BY a.assessmentName, aba.batches.id, aba.assessmentStatus")
    List<AssessmentDetailsDTO> fetchAssessmentDetails();

    @Query("SELECT new com.ILPex.DTO.AssessmentDetailsDTO(" + "a.assessmentName, " + "aba.batches.id, " +  // Fetch batchId
            "CASE WHEN aba.assessmentStatus = true THEN 'Completed' ELSE 'Active' END, " + "COUNT(r.traineeId)) " + "FROM Assessments a " + "JOIN a.assessmentBatchAllocations aba " + "LEFT JOIN aba.results r " + "WHERE aba.batches.id = :batchId " + "GROUP BY a.assessmentName, aba.batches.id, aba.assessmentStatus")
    List<AssessmentDetailsDTO> fetchAssessmentDetailsByBatchId(Long batchId);

    @Query("SELECT new com.ILPex.DTO.TraineePendingAssessmentDTO(a.assessmentName, a.dueDate, t.id) " + "FROM Assessments a " + "JOIN a.assessmentBatchAllocations aba " + "JOIN aba.batches b " + "JOIN b.trainees t " + "LEFT JOIN Results r ON aba.id = r.assessmentBatchAllocation.id AND r.traineeId = t.id " + "WHERE r.id IS NULL AND t.id = :traineeId")
    List<TraineePendingAssessmentDTO> findPendingAssessmentsByTraineeId(@Param("traineeId") int traineeId);

    Assessments findByAssessmentName(String assessmentName);
}