package com.ILPex.repository;

import com.ILPex.DTO.AssessmentReportDTO;
import com.ILPex.entity.Assessments;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AssessmentsRepository extends JpaRepository<Assessments, Long> {
    @Query(value = "SELECT " +
            "a.assessment_name AS assessmentName, " +
            "aba.assessment_status AS assessmentStatus, " +
            "b.batch_name AS batchName, " +
            "COUNT(r.trainee_id) AS numberOfStudentsAttended, " +
            "u.username AS traineeName, " +
            "CASE " +
            "    WHEN r.result_id IS NOT NULL THEN 'ATTENDED' " +
            "    ELSE 'NOT ATTENDED' " +
            "END AS traineeStatus, " +
            "r.score " +
            "FROM assessments a " +
            "JOIN assessment_batch_allocation aba ON a.id = aba.assessment_id " +
            "JOIN batches b ON aba.batch_id = b.id " +
            "JOIN results r ON aba.id = r.assessment_batches_allocation_id " +
            "JOIN trainees t ON r.trainee_id = t.id " +
            "JOIN users u ON t.user_id = u.id " +
            "WHERE b.id = :batchId " +
            "AND (:status IS NULL OR " +
            "     (:status = 'active' AND aba.assessment_status = TRUE) OR " +
            "     (:status = 'completed' AND aba.assessment_status = FALSE)) " +
            "GROUP BY a.assessment_name, aba.assessment_status, b.batch_name, u.username, r.score, r.result_id " +
            "ORDER BY a.assessment_name, u.username",
            countQuery = "SELECT COUNT(*) FROM assessments a " +
                    "JOIN assessment_batch_allocation aba ON a.id = aba.assessment_id " +
                    "JOIN batches b ON aba.batch_id = b.id " +
                    "JOIN results r ON aba.id = r.assessment_batches_allocation_id " +
                    "JOIN trainees t ON r.trainee_id = t.id " +
                    "JOIN users u ON t.user_id = u.id " +
                    "WHERE b.id = :batchId " +
                    "AND (:status IS NULL OR " +
                    "     (:status = 'active' AND aba.assessment_status = TRUE) OR " +
                    "     (:status = 'completed' AND aba.assessment_status = FALSE)) ",
            nativeQuery = true)
    Page<Object[]> getAssessmentDetailsByBatchIdAndStatus(Long batchId, String status, Pageable pageable);




}
