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
                        ((Number) result[2]).longValue(), // numberOfStudentsAttended
                        (String) result[3], // traineeName
                        (String) result[4], // traineeStatus
                        ((Number) result[5]).intValue() // score
                ))
                .collect(Collectors.toList());
    }
}
