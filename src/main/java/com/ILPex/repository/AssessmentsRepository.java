package com.ILPex.repository;

import com.ILPex.DTO.AssessmentReportDTO;
import com.ILPex.entity.Assessments;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AssessmentsRepository extends JpaRepository<Assessments, Long> {
    @Query(value = "SELECT " +
            "a.assessment_name AS assessmentName, " +
            "aba.assessment_status AS assessmentStatus, " +
            "b.batch_name AS batchName, " + // Added batchName
            "COUNT(r.trainee_id) AS numberOfStudentsAttended, " +
            "u.username AS traineeName, " +
            "CASE " +
            "    WHEN r.result_id IS NOT NULL THEN 'ATTENDED' " +
            "    ELSE 'NOT ATTENDED' " +
            "END AS traineeStatus, " +
            "r.score " +
            "FROM assessments a " +
            "JOIN assessment_batch_allocation aba ON a.id = aba.assessment_id " +
            "JOIN batches b ON aba.batch_id = b.id " + // Added join with batches
            "JOIN results r ON aba.id = r.assessment_batches_allocation_id " +
            "JOIN trainees t ON r.trainee_id = t.id " +
            "JOIN users u ON t.user_id = u.id " +
            "GROUP BY a.assessment_name, aba.assessment_status, b.batch_name, u.username, r.score, r.result_id " + // Added batchName to GROUP BY
            "ORDER BY a.assessment_name, u.username", nativeQuery = true)
    List<Object[]> getAssessmentDetailsNative();




}
