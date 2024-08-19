package com.ILPex.service.Impl;

import com.ILPex.DTO.AssessmentReportDTO;
import com.ILPex.repository.AssessmentsRepository;
import com.ILPex.service.AssessmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AssessmentServiceImpl implements AssessmentService{

    @Autowired
    private AssessmentsRepository assessmentRepository;


    @Override
    public List<AssessmentReportDTO> getAssessmentDetails() {
        List<Object[]> results = assessmentRepository.getAssessmentDetailsNative();
        return results.stream()
                .map(result -> new AssessmentReportDTO(
                        (String) result[0], // assessmentName
                        (Boolean) result[1], // assessmentStatus
                        (String) result[2], // batchName
                        ((Number) result[3]).longValue(), // numberOfStudentsAttended
                        (String) result[4], // traineeName
                        (String) result[5], // traineeStatus
                        (Integer) result[6]
                ))
                .collect(Collectors.toList());
    }
}
