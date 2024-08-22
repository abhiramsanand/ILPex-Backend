package com.ILPex.service;

import com.ILPex.DTO.*;
import org.springframework.data.domain.Page;

import java.util.List;

public interface AssessmentService {
    TraineeAssessmentDisplayDTO getAssessmentById(Long assessmentId);
    List<TraineeCompletedAssessmentDTO> getCompletedAssessmentsByTraineeId(int traineeId);
    List<TraineePendingAssessmentDTO> getPendingAssessmentsByTraineeId(int traineeId);
}
