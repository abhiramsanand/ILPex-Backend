package com.ILPex.repository;

import com.ILPex.entity.PercipioAssessment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PercipioAssessmentRepository extends JpaRepository<PercipioAssessment, Long> {
    List<PercipioAssessment> findByTraineesId(Long traineeId);
}
