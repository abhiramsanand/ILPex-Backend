package com.ILPex.repository;

import com.ILPex.entity.AssessmentBatchAllocation;
import com.ILPex.entity.Assessments;
import com.ILPex.entity.Batches;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AssessmentBatchAllocationRepository extends JpaRepository<AssessmentBatchAllocation, Long> {
    AssessmentBatchAllocation findByAssessmentsAndBatches(Assessments assessments, Batches batches);
}
