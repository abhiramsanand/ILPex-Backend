package com.ILPex.service;

import com.ILPex.DTO.AssessmentReportDTO;
import org.springframework.data.domain.Page;

import java.util.List;

public interface AssessmentService {

    Page<AssessmentReportDTO> getAssessmentDetailsByBatchIdAndStatus(Long batchId, String status, int page, int size);

}
