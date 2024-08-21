package com.ILPex.repository;

import com.ILPex.DTO.AssessmentDetailsDTO;
import com.ILPex.entity.Assessments;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AssessmentsRepository extends JpaRepository<Assessments, Long> {


    @Query("SELECT new com.ILPex.DTO.AssessmentDetailsDTO(a.assessmentName, " +
            "CASE WHEN aba.assessmentStatus = true THEN 'Completed' ELSE 'Pending' END, " +
            "COUNT(r.traineeId)) " +
            "FROM Assessments a " +
            "JOIN a.assessmentBatchAllocations aba " +
            "LEFT JOIN aba.results r " +
            "GROUP BY a.assessmentName, aba.assessmentStatus")
    List<AssessmentDetailsDTO> fetchAssessmentDetails();



}