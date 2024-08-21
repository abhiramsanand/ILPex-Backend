package com.ILPex.service;

import com.ILPex.DTO.AssessmentReportDTO;
import com.ILPex.DTO.TraineeAssessmentDTO;
import com.ILPex.DTO.TraineeAssessmentDisplayDTO;
import com.ILPex.DTO.TraineeAssessmentSubmissionDTO;
import org.springframework.data.domain.Page;

import java.util.List;

public interface AssessmentService {

    Page<AssessmentReportDTO> getAssessmentDetailsByBatchIdAndStatus(Long batchId, String status, int page, int size);
    List<TraineeAssessmentDTO> getAssessmentsByTraineeId(int traineeId);
    TraineeAssessmentDisplayDTO getAssessmentById(Long assessmentId);

    boolean submitAssessment(TraineeAssessmentSubmissionDTO submissionDTO);
}
