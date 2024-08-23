package com.ILPex.service;

import com.ILPex.DTO.AssessmentCreationDTO;
import org.springframework.web.multipart.MultipartFile;

public interface AssessmentCreation {

    void createAssessment(AssessmentCreationDTO assessmentCreationDTO) throws Exception;

    AssessmentCreationDTO parseExcelFile(MultipartFile file, String title, Long batchId, String startDate, String endDate) throws Exception;

}
