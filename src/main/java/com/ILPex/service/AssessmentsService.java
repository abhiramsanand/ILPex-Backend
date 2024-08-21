package com.ILPex.service;

import com.ILPex.DTO.AssessmentDetailsDTO;

import java.util.List;

public interface AssessmentsService {

    List<AssessmentDetailsDTO> getAssessmentDetails();
    List<AssessmentDetailsDTO> getAssessmentDetailsByBatchId(Long batchId);
}
